package com.example.jocs_guinart.Games

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.example.jocs_guinart.R
import com.example.jocs_guinart.TBL_POS

class Dames : AppCompatActivity() {

    private var table : Array<Array<TBL_POS>> = Array(8) { Array<TBL_POS>(8) { TBL_POS.EMPTY } };
    private lateinit var btns : Array<Array<ImageButton>>;// = Array(8) { Array<ImageButton>(8){ null } }; // ImageButton btn[8][8];
    private var playerTurn : Boolean = true;
    private var userFirst : Boolean = true;

    //Stats
    private lateinit var uid : String;
    private var games : Int = 0;
    private var wins : Int = 0;
    private var losses : Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dames)

        setupBtn();
    }

    private fun setupBtn()
    {
        btns = Array(8) { Array<ImageButton>(8){ findViewById(R.id.b00) } };
        var i: Int = 0;
        var j: Int = 0;
        while(i < 8)
        {
            j = 0
            while(j < 8)
            {
                val btnId = resources.getIdentifier("b$i$j", "id", packageName);
                btns[i][j] = findViewById<ImageButton>(btnId);
                btns[i][j].setOnClickListener {
                    val str : String = it.tag as String;
                    val btnI : Int = str[0].digitToInt();
                    val btnJ : Int = str[1].digitToInt();

                    tapButton(btnI, btnJ);
                }
                j++
            }
            i++;
        }
    }





    /**
     *  Afegeix una fitxa a table[i][j].
     */
    private fun tapButton(i : Int, j : Int)
    {
        btns[i][j].isEnabled = false;
        if(table[i][j] != TBL_POS.AVAILABLE)
        {
            Log.e("ERR", "Posició invàlida");
            return;
        }

        //changePos(i, j, getCurPlayer()); // Col·loca una fitxa a la posició
        //checkPosition(i, j, TBL_GOAL.VICI);  // Gira les fitxes que es poden girar

        //newTurn();
    }
}