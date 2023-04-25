package com.example.jocs_guinart.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jocs_guinart.R

class RankingAdapter(private val playerList : ArrayList<RankingPlayer>) : RecyclerView.Adapter<RankingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        return RankingViewHolder(layoutInflater.inflate(R.layout.item_player, parent, false));
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = playerList[position];
        holder.render(item);
    }

    override fun getItemCount(): Int {
        return playerList.size;
    }
}