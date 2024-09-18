package com.example.cornerpocket.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.ItemOpponentBinding
import com.example.cornerpocket.models.Opponent


//https://academy.realm.io/posts/android-recycler-view/
//https://github.com/realm/realm-android-adapters?tab=readme-ov-file

class OpponentSelectorAdapter(private var opponents: MutableList<Opponent>, var context: Context, var fragmentId: Int? = null)
    : RecyclerView.Adapter<OpponentSelectorAdapter.OpponentsViewHolder>() {
    inner class OpponentsViewHolder(val binding: ItemOpponentBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClicked : ((Opponent) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpponentsViewHolder {
        val view = ItemOpponentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return OpponentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return opponents.size
    }

    //Used for removing the tick icon from the previously selected opponent
    private var previousPositionIndex : Int? = null

    override fun onBindViewHolder(holder: OpponentsViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.apply {
            tvOpponentName.text = opponents[position].name

            val pfp = ImageUtils.getImageFromLocalStorage(context, opponents[position]._id.toString())
            if (pfp != null){
                ivOpponentImage.setImageURI(pfp)
            } else {
                ivOpponentImage.setImageResource(R.drawable.user_icon_red_no_circle)
            }

            if (fragmentId != R.id.opponentDetailsFragment){
                if (opponents[position].recyclerOpponentSelected){
                    ivSelectedItem.visibility = View.VISIBLE
                } else {
                    ivSelectedItem.visibility = View.INVISIBLE
                }
            }

        }

        /**
         * If there is a previously selected item, deselect it and update the var
         */
        holder.itemView.setOnClickListener {
            //Pass back the opponent so the on click listener in the fragment can use it
            onItemClicked?.invoke(opponents[position])

            if (previousPositionIndex != null){
                opponents[previousPositionIndex!!].recyclerOpponentSelected = false
                notifyItemChanged(previousPositionIndex!!)
            }
            previousPositionIndex = position
            opponents[position].recyclerOpponentSelected = true
            notifyItemChanged(position)
        }

    }

}