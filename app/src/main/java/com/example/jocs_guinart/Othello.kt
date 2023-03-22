package com.example.jocs_guinart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast

enum class POS
{
    EMPTY,
    AVAILABLE,
    WHITE,
    BLACK
};

enum class GOAL
{
    VENI, // Venir: trobar posicions disponibles
    VIDI, // Veure: trobar el millor moviment
    VICI  // Vèncer: executar el moviment

}
enum class DIR
{
    NW, N, NE,
    W,      E,
    SW, S, SE
}
class Othello() : AppCompatActivity() {

    private var table : Array<Array<POS>> = Array(8) { Array<POS>(8) { POS.EMPTY } };
    private lateinit var btns : Array<Array<ImageButton>>;// = Array(8) { Array<ImageButton>(8){ null } }; // ImageButton btn[8][8];
    private var playerTurn : Boolean = true;
    private var userFirst : Boolean = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_othello);

        val extras : Bundle? = intent.extras;
        /*if(extras == null)
        {
            finish();
            return;
        }*/

        setupBtn();

        changePos(3, 3, POS.BLACK);
        changePos(3, 4, POS.WHITE);
        changePos(4, 3, POS.WHITE);
        changePos(4, 4, POS.BLACK);

        checkPosition(3, 3, GOAL.VENI);
        checkPosition(4, 4, GOAL.VENI)


        userFirst = true;//extras.get("playerFirst") as Boolean;
        playerTurn = userFirst;

        if(!playerTurn)
        {
            machineLogic();
        }
    }

    /**
     *  Retorna si el jugador és blanques o negres.
     *  A l'Othello, les negres sempre són les primeres.
     */
    private fun getCurPlayer(): POS
    {
        return if(userFirst) if(playerTurn) POS.BLACK else POS.WHITE;
               else          if(playerTurn) POS.WHITE else POS.BLACK;
    }

    /**
     *  Busca i guarda tots els botons dins de l'array de dues dimensions.
     */
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

    private fun changePos(i: Int, j : Int, newState : POS)
    {
        table[i][j] = newState;

        when(newState)
        {
            POS.WHITE     ->
            {
                btns[i][j].setImageResource(R.drawable.white);
                btns[i][j].isEnabled = false;
            }
            POS.BLACK     ->
            {
                btns[i][j].setImageResource(R.drawable.black)
                btns[i][j].isEnabled = false;
            }
            POS.AVAILABLE ->
            {
                btns[i][j].setImageResource(R.drawable.custom_button)
                btns[i][j].isEnabled = true;
            }
            POS.EMPTY     ->
            {
                btns[i][j].setImageResource(R.drawable.transparent)
                btns[i][j].isEnabled = false;
            }
        }
    }


    private fun  newTurn()
    {
        playerTurn = !playerTurn;

        //Reinicia els llocs vàlids.
        var i : Int = 0;
        var j : Int = 0;
        while (i < 8)
        {
            j = 0;
            while (j < 8)
            {
                if(table[i][j] == POS.AVAILABLE)
                {
                    changePos(i, j, POS.EMPTY);
                }
                j++;
            }
            i++;
        }

        val curPlayer : POS = getCurPlayer();

        i = 0;
        j = 0;
        var canMove : Boolean = false;
        //Busca els llocs vàlids pel nou jugador
        while(i < table.size)
        {
            j = 0;
            while(j < table[0].size)
            {
                if(table[i][j] == curPlayer) {
                    if(checkPosition(i, j, GOAL.VENI)) // Comprova cada fitxa del jugador a qui li toca
                        canMove = true;
                }
                j++;
            }
            i++;
        }

        if(!canMove)
        {
            endGame();
            return;
        }

        if(!playerTurn)
        {
            machineLogic();
        }
    }

    /**
     *      Comprova les 8 posicions al voltant d'una peça. Si són del rival, les
     *      segueix fins trobar una posició lliure, la qual serà un moviment legal.
     *
     *      @param curPlayer Jugador que fa la jugada
     *      @param goal Objectiu de la lògica.  VENI: buscar jugades disponibles
     *                                          VIDI: girar peces
     *                                          VICI: Buscar el millor moviment immediat (AI)

     *      @return En el cas que goal == VENI, serà diferent a 0 si existeixen posicions disponibles,
     *              en el cas que goal == VIDI, serà el nombre de fitxes que es mourien amb aquesta jugada.
     *              En tots casos, retorna -1 en error.
     *              TODO: implementar aquests retorns i l'opció VICI
     */
    private fun checkPosition(i : Int, j : Int, goal : GOAL) : Boolean
    {
        val curPlayer = getCurPlayer();

        val rival : POS = if(curPlayer == POS.BLACK) POS.WHITE else POS.BLACK;
        var canMove : Boolean = false;

        if( i > 0 ) // Primera fila
        {
            if(j > 0 && table[i-1][j-1] == rival)
                if(chkPosLogic(i-1, j-1, goal, DIR.NW))
                    canMove = true;

            if(table[i-1][j] == rival)
                if(chkPosLogic(i-1, j, goal, DIR.N))
                    canMove = true;

            if(j < table[0].size - 1 && table[i-1][j+1] == rival)
                if(chkPosLogic(i-1, j+1, goal, DIR.NE))
                    canMove = true;
        }

        if( j > 0 && table[i][j-1] == rival)
            if(chkPosLogic(i, j-1, goal, DIR.W))
                canMove = true;

        if( j < table[0].size - 1 && table[i][j+1] == rival)
            if(chkPosLogic(i, j+1, goal, DIR.E))
                canMove = true;


        if( i < table.size - 1 ) // Última fila
        {
            if( j > 0 && table[i+1][j-1] == rival)
                if(chkPosLogic(i+1, j-1, goal, DIR.SW))
                    canMove = true;

            if( table[i+1][j] == rival)
                if(chkPosLogic(i+1, j, goal, DIR.S))
                    canMove = true;

            if(j < table[0].size - 1 && table[i+1][j+1] == rival)
                if(chkPosLogic(i+1, j+1, goal, DIR.SE))
                    canMove = true;
        }

        return canMove;
    }

    /**
     * Lògica de cada posició vora la fitxa trobada. Aquesta funció evita repetir el seu codi vuit vegades.
     */
    private fun chkPosLogic(i : Int, j : Int, goal : GOAL, dir : DIR) : Boolean
    {
        val rival = if(getCurPlayer() == POS.BLACK) POS.WHITE else POS.BLACK;
        val end = followPiece(rival, i, j, dir);

        if(end == -1) return false;
        when(goal)
        {
            GOAL.VENI -> {
                if(table[end/8][end%8] == POS.EMPTY)
                {
                    changePos(end/8, end%8, POS.AVAILABLE);
                    return true;
                }
            }
            GOAL.VIDI -> Log.e("TODO", "Handle VIDI"); //TODO
            GOAL.VICI -> turnPieces(i, j, end/8, end%8);
        }
        return false;
    }

    /**
     *  Segueix les peces del rival fins trobar-ne una diferent.
     *
     *  @param rival Fitxes del rival.
     *  @param direction Direcció cardinal on seguir les fitxes
     *
     *  @return Número indicant la posició on acaba la seqüència de peces enemigues, com i*8+j
     *          Si troba el final del taulell, retorna -1
     */
    private fun followPiece(rival : POS, curI : Int, curJ : Int, direction : DIR): Int
    {
        //Trobem la següent posició
        val nextI : Int = when(direction) {
            DIR.NW, DIR.N, DIR.NE -> curI-1; // UP (N)
            DIR.E,         DIR.W  -> curI;
            DIR.SW, DIR.S, DIR.SE -> curI+1; // DOWN (S)
        }
        val nextJ : Int = when(direction) {
            DIR.NW, DIR.W, DIR.SW -> curJ-1; // LEFT (E)
            DIR.N,         DIR.S  -> curJ;
            DIR.NE, DIR.E, DIR.SE -> curJ+1; // RIGHT (W)
        }

        if(nextI < 0 || nextI > 7 || nextJ < 0 || nextJ > 7)
            return -1;

        if(table[nextI][nextJ] == rival)
            return followPiece(rival, nextI, nextJ, direction);
        return nextI*8 +nextJ;
    }

    /**
     * Afegeix una fitxa a table[i][j].
     */
    private fun tapButton(i : Int, j : Int)
    {
        btns[i][j].isEnabled = false;
        if(table[i][j] != POS.AVAILABLE)
        {
            Log.e("ERR", "Posició invàlida");
            return;
        }

        changePos(i, j, getCurPlayer()); // Col·loca una fitxa a la posició
        checkPosition(i, j, GOAL.VICI);   // Gira les fitxes que es poden girar

        newTurn();
    }

    private fun turnPieces(startI : Int, startJ : Int, endI : Int, endJ: Int)
    {
        if(endJ < 0) // Posició invàlida
            return;

        val curPlayer = getCurPlayer();

        if(table[endI][endJ] != curPlayer) // La posició trobada no és del jugador
            return;

        //Direcció de startPos a endPos
        val dirI : Int = if(endI == startI) 0 else if(endI > startI) 1 else -1;
        val dirJ : Int = if(endJ == startJ) 0 else if(endJ > startJ) 1 else -1;

        var i : Int = startI;
        var j : Int = startJ;
        while (table[i][j] != curPlayer)
        {
            changePos(i, j, curPlayer);
            j+=dirJ;
            i+=dirI;
            if(i < 0 || i > 7 || j < 0 || j > 7) break;
        }
    }

    private fun machineLogic()
    {
//        newTurn();
        return;
    }

    private fun endGame()
    {
        Log.i("END", "Game finished");
        var white : Int = 0;
        var black : Int = 0;

        //Compta les peces finals.
        var i : Int = 0;
        var j : Int = 0;
        while(i < table.size)
        {
            j = 0;
            while(j < table[0].size)
            {
                if(table[i][j] == POS.AVAILABLE) changePos(i, j, POS.EMPTY);
                if(table[i][j] == POS.BLACK) black++;
                if(table[i][j] == POS.WHITE) white++;

                j++;
            }
            i++;
        }

        //TODO: win conditions
        val winner : String = if(white > black) "blanc" else "negre";
        Toast.makeText(this, "El guanyador és $winner!", Toast.LENGTH_LONG).show();


    }
}