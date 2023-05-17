package com.example.jocs_guinart.Games

import com.example.jocs_guinart.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.jocs_guinart.DIR
import com.example.jocs_guinart.TBL_POS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*



enum class OTH_GOAL
{
    VENI, // Venir: trobar posicions disponibles
    VIDI, // Veure: trobar el millor moviment
    VICI  // Vèncer: executar el moviment
}

class Othello() : AppCompatActivity() {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_othello);

        val extras:Bundle? = intent.extras;

        games  = extras?.get("TOT_GAMES") as Int;
        Log.d("DEBUG", "$games -> $wins : $losses");
        if(games == 0) //És la primera vegada que entrem aquí.
            getDBData();
        else
        {
            uid    = extras.get("UID") as String;
            wins   = extras.get("WINS") as Int;
            losses = extras.get("LOSSES") as Int;
        }
        userFirst = extras.get("playerFirst") as Boolean;
        playerTurn = userFirst;

        setupBtn();

        //Posicions inicials
        changePos(3, 3, TBL_POS.BLACK);
        changePos(3, 4, TBL_POS.WHITE);
        changePos(4, 3, TBL_POS.WHITE);
        changePos(4, 4, TBL_POS.BLACK);

        checkPosition(3, 3, OTH_GOAL.VENI);
        checkPosition(4, 4, OTH_GOAL.VENI)

        if(!playerTurn)
            machineLogic();
    }

    /**
     *  Retorna si el jugador és blanques o negres.
     *  A l'Othello, les negres sempre són les primeres.
     */
    private fun getCurPlayer(): TBL_POS
    {
        return if(userFirst) if(playerTurn) TBL_POS.BLACK else TBL_POS.WHITE;
               else          if(playerTurn) TBL_POS.WHITE else TBL_POS.BLACK;
    }

    /**
     *  Busca i guarda tots els botons dins d'un array bidimensional.
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

    /**
     *  Canvia l'estat d'una peça.
     *  En el cas de POS.AVAILABLE, només canviarà gràficament si és el torn del jugador.
     */
    private fun changePos(i: Int, j : Int, newState : TBL_POS)
    {
        table[i][j] = newState;

        when(newState)
        {
            TBL_POS.WHITE     ->
            {
                btns[i][j].setImageResource(R.drawable.white);
                btns[i][j].isEnabled = false;
            }
            TBL_POS.BLACK     ->
            {
                btns[i][j].setImageResource(R.drawable.black)
                btns[i][j].isEnabled = false;
            }
            TBL_POS.AVAILABLE ->
            {
                if(playerTurn) { // No ho ensenyis quan sigui el torn de la màquina
                    btns[i][j].setImageResource(R.drawable.custom_button)
                    btns[i][j].isEnabled = true;
                }
            }
            TBL_POS.EMPTY     ->
            {
                btns[i][j].setImageResource(R.drawable.transparent)
                btns[i][j].isEnabled = false;
            }
        }
    }


    /**
     *  Nou torn:
     *      Reinicia totes les posicion lliures (POS.AVAILABLE -> POS.EMPTY)
     *      Troba noves posicions.
     *      Si no n'hi ha cap, s'ha acabat el joc.
     *      Si no és el torn del jugador, crida al codi de la màquina.
     */
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
                if(table[i][j] == TBL_POS.AVAILABLE)
                {
                    changePos(i, j, TBL_POS.EMPTY);
                }
                j++;
            }
            i++;
        }

        val curPlayer : TBL_POS = getCurPlayer();

        i = 0;
        var canMove : Boolean = false;
        //Busca els llocs vàlids pel nou jugador
        while(i < table.size)
        {
            j = 0;
            while(j < table[0].size)
            {
                if(table[i][j] == curPlayer) {
                    if(checkPosition(i, j, OTH_GOAL.VENI) > 0) // Comprova cada fitxa del jugador a qui li toca
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
     */
    private fun checkPosition(i : Int, j : Int, goal : OTH_GOAL) : Int
    {
        val curPlayer = getCurPlayer();

        val rival : TBL_POS = if(curPlayer == TBL_POS.BLACK) TBL_POS.WHITE else TBL_POS.BLACK;
        var res : Int = 0;

        if( i > 0 ) // Primera fila
        {
            if(j > 0 && table[i-1][j-1] == rival) {
                val aux = chkPosLogic(i - 1, j - 1, goal, DIR.NW);
                if(aux == -1) return -1; // En cas d'error, retorna immediatament.
                if(aux > res) res += aux;
            }

            if(table[i-1][j] == rival) {
                val aux = chkPosLogic(i - 1, j, goal, DIR.N);
                if(aux == -1) return -1;
                if(aux > res) res += aux;
            }

            if(j < table[0].size - 1 && table[i-1][j+1] == rival) {
                val aux = chkPosLogic(i - 1, j + 1, goal, DIR.NE);
                if(aux == -1) return -1;
                if(aux > res) res += aux;
            }
        }

        if( j > 0 && table[i][j-1] == rival){
            val aux = chkPosLogic(i, j - 1, goal, DIR.W);
            if(aux == -1) return -1;
            if(aux > res) res += aux;
        }

        if( j < table[0].size - 1 && table[i][j+1] == rival) {
            val aux = chkPosLogic(i, j + 1, goal, DIR.E);
            if(aux == -1) return -1;
            if(aux > res) res += aux;
        }


        if( i < table.size - 1 ) // Última fila
        {
            if( j > 0 && table[i+1][j-1] == rival){
                val aux = chkPosLogic(i + 1, j - 1, goal, DIR.SW);
                if(aux == -1) return -1;
                if(aux > res) res += aux;
            }

            if( table[i+1][j] == rival){
                val aux = chkPosLogic(i + 1, j, goal, DIR.S);
                if(aux == -1) return -1;
                if(aux > res) res += aux;
            }

            if(j < table[0].size - 1 && table[i+1][j+1] == rival){
                val aux = chkPosLogic(i + 1, j + 1, goal, DIR.SE);
                if(aux == -1) return -1;
                if(aux > res) res += aux;
            }
        }

        return res;
    }

    /**
     * Lògica de cada posició vora la fitxa trobada. Aquesta funció evita repetir el seu codi vuit vegades.
     */
    private fun chkPosLogic(i : Int, j : Int, goal : OTH_GOAL, dir : DIR) : Int
    {
        val rival = if(getCurPlayer() == TBL_POS.BLACK) TBL_POS.WHITE else TBL_POS.BLACK;
        val end = followPiece(rival, i, j, dir);
        val endI : Int = end/8;
        val endJ : Int = end%8;

        if(end == -1)
        {
            Log.e("ERR", "chkPosLogic returned -1. ([$i][$j] $dir $goal -> end = $end");
            return -1;
        };
        when(goal)
        {
            OTH_GOAL.VENI -> {
                if(table[endI][endJ] == TBL_POS.EMPTY)
                {
                    changePos(endI, endJ, TBL_POS.AVAILABLE);
                    return 1;
                }
            }
            OTH_GOAL.VIDI -> return countPieces(i, j, endI, endJ);
            OTH_GOAL.VICI ->  turnPieces(i, j, endI, endJ);
        }
        return 0;
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
    private fun followPiece(rival : TBL_POS, curI : Int, curJ : Int, direction : DIR): Int
    {

        //Direcció de startPos a endPos
        val dirI : Int = when(direction) {
            DIR.NW, DIR.N, DIR.NE -> -1; // UP (N)
            DIR.E,         DIR.W  ->  0;
            DIR.SW, DIR.S, DIR.SE ->  1; // DOWN (S)
        }
        val dirJ : Int = when(direction) {
            DIR.NW, DIR.W, DIR.SW -> -1; // LEFT (E)
            DIR.N,         DIR.S  ->  0;
            DIR.NE, DIR.E, DIR.SE ->  1; // RIGHT (W)
        }

        var i : Int = curI;
        var j : Int = curJ;
        while (table[i][j] == rival)
        {
            if(i+dirI < 0 || i+dirI > 7 || j+dirJ < 0 || j+dirJ > 7)
                break;

            j+=dirJ;
            i+=dirI;
        }

        return i*8 +j;
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

        changePos(i, j, getCurPlayer()); // Col·loca una fitxa a la posició
        checkPosition(i, j, OTH_GOAL.VICI);  // Gira les fitxes que es poden girar

        newTurn();
    }

    /**
     *  Compta les fitxes que es girarien en una certa jugada en una certa direcció.s
     */
    private fun countPieces(startI : Int , startJ : Int, endI : Int, endJ : Int): Int
    {
        val curPlayer = getCurPlayer();
        var count = 0;

        if(table[endI][endJ] != curPlayer) // La posició trobada no és del jugador
            return 0;

        //Direcció de startPos a endPos
        val dirI : Int = if(endI == startI) 0 else if(endI > startI) 1 else -1;
        val dirJ : Int = if(endJ == startJ) 0 else if(endJ > startJ) 1 else -1;

        var i : Int = startI;
        var j : Int = startJ;
        while (table[i][j] != curPlayer)
        {
            count++;
            if(i+dirI < 0 || i+dirI > 7 || j+dirJ < 0 || j+dirJ > 7)
                break;
            j+=dirJ;
            i+=dirI;

        }
        return count;
    }

    /**
     *  Gira les peces d'una línia
     */
    private fun turnPieces(startI : Int, startJ : Int, endI : Int, endJ: Int)
    {
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
            if(i+dirI < 0 || i+dirI > 7 || j+dirJ < 0 || j+dirJ > 7)
                break;

            j+=dirJ;
            i+=dirI;
        }
    }

    /**
     *  AI
     *  Troba la posició que gira més peces del jugador i l'executa.
     */
    private fun machineLogic()
    {
        var curMov   : Int = 0;
        var bestMov  : Int = 0;
        var bestPosI : Int = 0;
        var bestPosJ : Int = 0;
        var count    : Int = 0;

        val strtTime : Long = System.currentTimeMillis();

        //Trobar millor posició
        var i : Int = 0;
        var j : Int = 0;
        while(i < table.size)
        {
            j = 0
            while(j < table[0].size)
            {
                if(table[i][j] == TBL_POS.AVAILABLE)
                {
                    curMov = checkPosition(i, j, OTH_GOAL.VIDI);
                    if(curMov > bestMov)
                    {
                        bestMov = curMov;
                        bestPosI = i;
                        bestPosJ = j;
                    }
                } else if(table[i][j] == TBL_POS.EMPTY)
                    count++;
                j++
            }
            i++
        }

        // Posar la peça
        Log.d("MCH", "best move: [$bestPosI][$bestPosJ]");

        //Esperar a que hagi passat un cert temps abans de tirar
        //Com més torns hagin passat, més temps tardarà. Dóna temps al jugador per veure què canvia.
        val waitTime : Long = strtTime + 100 * count / 60  + 1000 - System.currentTimeMillis();
        Handler().postDelayed({
            tapButton(bestPosI, bestPosJ);
        }, waitTime)
    }

    /**
     * Final de joc. Ensenya qui ha guanyat, quantes peces té, botons per tornar a jugar o sortir, etc.
     */
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
                if(table[i][j] == TBL_POS.AVAILABLE) changePos(i, j, TBL_POS.EMPTY);
                if(table[i][j] == TBL_POS.BLACK) black++;
                if(table[i][j] == TBL_POS.WHITE) white++;

                j++;
            }
            i++;
        }

        val winner : Int = if(userFirst) {
            if(black > white) 1
            else if(black < white) 2
            else 0;
        } else {
            if(white > black) 1
            else if(white < black) 2
            else 0;
        }

        updateDB(uid, winner);

        val totGames : TextView = findViewById<TextView>(R.id.totGames);
        val wonGames : TextView = findViewById<TextView>(R.id.wonGames);
        val lstGames : TextView = findViewById<TextView>(R.id.lostGames);
        val winnerT  : TextView = findViewById<TextView>(R.id.winner);
        val replayB  : Button = findViewById(R.id.replayB);
        val returnB  : Button = findViewById(R.id.backB);

        totGames.text = games.toString();
        wonGames.text = wins.toString();
        lstGames.text = losses.toString();

        when (winner) {
            1    -> winnerT.text = "Has guanyat!"
            2    -> winnerT.text = "Has perdut!"
            else -> winnerT.text = "Empat!"
        };

        totGames.visibility = TextView.VISIBLE;
        wonGames.visibility = TextView.VISIBLE;
        lstGames.visibility = TextView.VISIBLE;
        winnerT.visibility  = TextView.VISIBLE;

        replayB.visibility  = TextView.VISIBLE;
        returnB.visibility  = TextView.VISIBLE;

        findViewById<TextView>(R.id.totGamesTXT).visibility  = TextView.VISIBLE;
        findViewById<TextView>(R.id.wonGamesTXT).visibility  = TextView.VISIBLE;
        findViewById<TextView>(R.id.lostGamesTXT).visibility = TextView.VISIBLE;

        replayB.setOnClickListener {
            val extras:Bundle? = intent.extras;
            val whoStarts : Boolean = userFirst;

            val intent= Intent(this, Othello::class.java);
            intent.putExtra("playerFirst", whoStarts);
            intent.putExtra("UID", uid);
            intent.putExtra("TOT_GAMES", games);    //Mai serà 0: evitem que torni a crear una connexió a la BBDD al començar
            intent.putExtra("WINS", wins);
            intent.putExtra("LOSSES", losses);

            startActivity(intent);
            finish();
        }

        returnB.setOnClickListener { finish(); } // Torna al menú principal
    }

    private fun getDBData()
    {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        val dbreference: DatabaseReference = database.getReference("OTHELLO");
        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val auth : FirebaseAuth = FirebaseAuth.getInstance();
                val user: FirebaseUser? = auth.currentUser;

                uid = user!!.uid;

                for (ds in snapshot.children) {
                    if (ds.key.toString() == user.uid) {

                        games = ds.child("TotalGames").value.toString().toInt();
                        wins = ds.child("Wins").value.toString().toInt();
                        losses = ds.child("Losses").value.toString().toInt();

                        break;
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB", "ERROR DATABASE CANCEL");
            }
        });
    }

    fun updateDB(uid:String, winner: Int)
    {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        val dbreference: DatabaseReference = database.getReference("OTHELLO");

        games++;
        if     (winner == 1) wins++;
        else if(winner == 2) losses++;

        val playerData : HashMap<String,Any> = HashMap<String, Any>();
        playerData["TotalGames"] = games;
        playerData["Wins"] = wins;
        playerData["Losses"] = losses;

        dbreference.child(uid).setValue(playerData);
    }
}