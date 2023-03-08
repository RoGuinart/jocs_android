package com.example.jocs_guinart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class TresEnRatlla : AppCompatActivity() {
    private var table: Array<IntArray> = Array(3) { IntArray(3) }; // Déu meu Senyor
    private lateinit var bts : Array<ImageButton>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tres_en_ratlla)
        table[2][1] = 1;

        bts = arrayOf(
            findViewById<ImageButton>(R.id.pos00),
            findViewById<ImageButton>(R.id.pos01),
            findViewById<ImageButton>(R.id.pos02),
            findViewById<ImageButton>(R.id.pos10),
            findViewById<ImageButton>(R.id.pos11),
            findViewById<ImageButton>(R.id.pos12),
            findViewById<ImageButton>(R.id.pos20),
            findViewById<ImageButton>(R.id.pos21),
            findViewById<ImageButton>(R.id.pos22)
        );


        bts[0].setOnClickListener() { pressPos(1, 0,0); }
        bts[1].setOnClickListener() { pressPos(1, 0,1); }
        bts[2].setOnClickListener() { pressPos(1, 0,2); }
        bts[3].setOnClickListener() { pressPos(1, 1,0); }
        bts[4].setOnClickListener() { pressPos(1, 1,1); }
        bts[5].setOnClickListener() { pressPos(1, 1,2); }
        bts[6].setOnClickListener() { pressPos(1, 2,0); }
        bts[7].setOnClickListener() { pressPos(1, 2,1); }
        bts[8].setOnClickListener() { pressPos(1, 2,2); }
    }

    /**
     *  Dibuixa a una posició
     *  @param player Jugador.  1 per usuari, 2 per màquina
     */
    fun pressPos(player:Int, posX:Int, posY:Int)
    {
        if(table[posX][posY] != 0) // No s'hauria de donar el cas
        {
            bts[3 * posX + posY].isEnabled = false;
            return;
        }
        table[posX][posY] = player;

        //TODO: canviar la imatge

        val end:Int=checkEnd();
        if(end != 0)
        {
            //TODO: lògica del final del joc
        }


    }

    /**
     *  Comprova si s'ha acabat el joc.
     *  @return 0 si no s'ha acabat el joc, -1 si s'ha acabat empatat o la ID del jugador (1, 2)
     */
    private fun checkEnd(): Int
    {
        if ( (table[0][0] == table[1][1] && table[1][1] == table[2][2])   //Diagonal \
          || (table[0][2] == table[1][1] && table[1][1] == table[2][0]) ) //Diagonal /
        {
            return table[1][1];
        }

        //Horitzontal i vertical
        var i:Int = 0;
        while(i < 3)
        {
            if ( (table[i][0] == table[i][1] && table[i][1] == table[i][2])   //Horitzontal
              || (table[0][i] == table[1][i] && table[1][i] == table[2][i]) ) //Vertical
            {
                return table[i][i];
            }
            i++
        }

        for (posX in table)
        {
            for(pos in posX)
            {
                if(pos == 0) //Encara es poden fer jugades
                    return 0;
            }
        }

        return -1;
    }


}