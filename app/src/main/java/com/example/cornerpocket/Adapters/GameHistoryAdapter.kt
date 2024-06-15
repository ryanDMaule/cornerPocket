package com.example.cornerpocket.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.ItemResultBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.MainViewModel
import com.example.cornerpocket.HelperFunctions


class GameHistoryAdapter(private var games: MutableList<Game>, var vm : MainViewModel) : RecyclerView.Adapter<GameHistoryAdapter.GamesViewHolder>()  {
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
            if (games[position].userWon){
                result.text = "WIN"
                result.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            } else {
                result.text = "LOSS"
                result.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            }

            val opponent = vm.getOpponentViaId(games[position].opponentId)
            name.text = opponent?.name

//            val gameDate = longConverison(games[position].date)
            val gameDate = HelperFunctions.longConversion(games[position].date)
            date.text = HelperFunctions.formatDateToString(gameDate)
        }

        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(games[position])
        }

    }

}