package com.example.jocs_guinart

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

enum class POS
{
    EMPTY,
    AVAILABLE,
    WHITE,
    BLACK
};
class Othello : AppCompatActivity() {

    private var table : Array<Array<POS>> = Array(8) { Array<POS>(8) { POS.EMPTY } };
    private var btns : Array<Array<ImageButton?>> = Array(8) { Array<ImageButton?>(8){ null } }; // ImageButton btn[8][8];
    private var playerTurn : Boolean = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_othello);

        table[3][3] = POS.BLACK;
        table[3][4] = POS.WHITE;
        table[4][3] = POS.WHITE;
        table[4][4] = POS.BLACK;

        setupBtn();

        //checkValidPos();

        if(!playerTurn)
        {
            //machineLogic(); // Recordem que hi haurà tres nivells. Pensa com fer-los!
        }
    }

    /**
     *  Busca i guarda tots els botons dins de l'array de dues dimensions.
     */
    private fun setupBtn()
    {
        var i: Int = 0;
        var j: Int = 0;
        while(i < 8)
        {
            j = 0
            while(j < 8)
            {
                val curBtn : String = "b$i$j";
                val btnId = resources.getIdentifier(curBtn, "id", packageName);
                btns[i][j] = findViewById<ImageButton>(btnId);
                btns[i][j]!!.setOnClickListener {
                    tapButton(i,j);
                }
                j++
            }
            i++;
        }
    }

    private fun tapButton(i : Int, j : Int)
    {
        btns[i][j]?.isEnabled = false;
        if(table[i][j] != POS.AVAILABLE)
        {
            Log.e("ERR", "Posició invàlida");
            return;
        }
    }
}