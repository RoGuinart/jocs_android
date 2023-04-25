package com.example.jocs_guinart.Recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jocs_guinart.R
import com.squareup.picasso.Picasso

class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val pname   = view.findViewById<TextView>(R.id.rName);
    val pGames  = view.findViewById<TextView>(R.id.rGames);
    val pWins   = view.findViewById<TextView>(R.id.rWins);
    val pLosses = view.findViewById<TextView>(R.id.rLosses);

    val pImg = view.findViewById<ImageView>(R.id.rImg);

    fun render(player : RankingPlayer)
    {
        pname.text   = player.username;
        pGames.text  = player.totGames.toString();
        pWins.text   = player.wonGames.toString();
        pLosses.text = player.lostGames.toString();
        if(player.picture != null)
        {
            Picasso.get().load(player.picture).into(pImg);
        } else
            pImg.setImageResource(R.drawable.def_player);
    }
}
