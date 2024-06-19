package com.example.cornerpocket.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cornerpocket.models.Opponent

class OpponentListAdapter(context: Context, private val opponentList: MutableList<Opponent>) :
    ArrayAdapter<Opponent>(context, android.R.layout.simple_list_item_1, opponentList) {

    private val allItem = Opponent().apply { name = "All" }

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getCount(): Int {
        return opponentList.size + 1 // Add one for the "All" item
    }

    override fun getItem(position: Int): Opponent {
        return if (position == 0) allItem else opponentList[position - 1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position).name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position).name
        return view
    }


}