package com.example.cornerpocket.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.ItemResultBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.viewModels.FilterViewModel


class GameHistoryAdapter(private var games: MutableList<Game>, var vm: FilterViewModel, val context: Context) : RecyclerView.Adapter<GameHistoryAdapter.GamesViewHolder>()  {
    inner class GamesViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)
    var onItemClicked : ((Game) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val view = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GamesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.apply {
            //Set the result text
            if (games[position].userWon){
                result.text = context.getString(R.string.win)
                result.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            } else {
                result.text = context.getString(R.string.loss)
                result.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            }

            //Set opponent name text
            val opponent = vm.getOpponentViaId(games[position].opponentId)
            name.text = opponent?.name

            //Set profile image
            val pfp = ImageUtils.getImageFromLocalStorage(context, opponent?._id.toString())
            if (pfp != null){
                opponentIcon.setImageURI(pfp)
            } else {
                opponentIcon.setImageResource(R.drawable.user_icon_red_no_circle)
            }

            //Show game date
            val gameDate = HelperFunctions.longConversion(games[position].date)
            date.text = HelperFunctions.formatDateToString(gameDate)
        }

        //Allow the on click to be handled within the fragment
        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(games[position])
        }

    }

}