package com.example.cornerpocket.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.ItemOpponentBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import io.realm.kotlin.query.RealmResults


//https://academy.realm.io/posts/android-recycler-view/
//https://github.com/realm/realm-android-adapters?tab=readme-ov-file

class OpponentSelectorAdapter(private var opponents: MutableList<Opponent>)
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

    private var previousPositionIndex : Int? = null

    override fun onBindViewHolder(holder: OpponentsViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.apply {
            tvOpponentName.text = opponents[position].name

            if (opponents[position].recyclerOpponentSelected){
                ivSelectedItem.visibility = View.VISIBLE
            } else {
                ivSelectedItem.visibility = View.INVISIBLE
            }

        }

        holder.itemView.setOnClickListener {
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