package com.example.jocs_guinart.Recycler

import android.net.Uri

data class RankingPlayer
(
    val uid         : String,
    val username    : String,

    val totGames    : Int,
    val wonGames    : Int,
    val lostGames   : Int,

    val picture     : Uri?
)
{

}