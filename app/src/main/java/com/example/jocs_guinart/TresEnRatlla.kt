package com.example.jocs_guinart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.Random

class TresEnRatlla : AppCompatActivity() {
    private var table: Array<IntArray> = Array(3) { IntArray(3) }; // Déu meu Senyor
    private lateinit var bts : Array<ImageButton>;

    private var playerTurn:Boolean = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tres_en_ratlla);


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


        val intent:Bundle? = intent.extras;
        playerTurn = intent?.get("playerFirst") as Boolean;

        if(!playerTurn) changeTurn();
    }

    private fun changeTurn()
    {
        var end = checkEnd();
        if(end != 0)
        {
            endGame(end);
            return;
        }

        //Habilitem o desabilitem els botons depenent del torn
        for ((i, btn) in bts.withIndex()) {
            if (table[i / 3][i % 3] == 0)
                btn.isEnabled = playerTurn;
        }

        if (!playerTurn) // Torn de la màquina
        {
            machineLogic();
        } else {
            //TimeUnit.MILLISECONDS.sleep(250L);
        }
    }

    /**
     *  Lògica de la màquina
     */
    private fun machineLogic()
    {
        //Comprova si hi ha dos Os seguides -> tirar per guanyar
        var i: Int = findMove(2);
        if(i!=-1)
        {
            pressPos(2, i/3, i%3);
            return;
        }

        //Comprova si hi ha dos Xs seguides -> tirar per evitar que el jugador guanyi
        i = findMove(1);
        if(i!=-1)
        {
            pressPos(2, i/3, i%3);
            return;
        }

        //Si no hi ha cap posició lliure, agafa un lloc aleatori.
        do {
            i= Random().nextInt(9);
        } while (table[i/3][i%3] != 0);

        pressPos(2, i/3, i%3);
    }

    /**
     *  Troba si hi ha una jugada guanyadora de player, i posa una fitxa allà.
     *  Per tant, guanya la partida o evita perdre-la.
     *
     *  @param  player Jugador del qual buscar peces. 1 per l'usuari, 2 per la màquina.
     *  @return La posició en un sol número. Per exemple, la posició table[1][2] és 1*3+2 = 5.
     */
    private fun findMove(player:Int): Int
    {
        //Diagonal \
        if(table[0][0] == 0 && table[1][1] == table[2][2] && table[1][1] == player) return 0*3+0;       // [0][0]
        if(table[1][1] == 0 && table[0][0] == table[2][2] && table[2][2] == player) return 1*3+1;       // [1][1]
        if(table[2][2] == 0 && table[0][0] == table[1][1] && table[0][0] == player) return 2*3+2;       // [2][2]

        //Diagonal /
        if(table[0][2] == 0 && table[2][0] == table[1][1] && table[2][0] == player) return 0*3 +2;      // [0][2]
        if(table[1][1] == 0 && table[2][0] == table[0][2] && table[0][2] == player) return 1*3 +1;      // [1][1]
        if(table[2][0] == 0 && table[1][1] == table[0][2] && table[1][1] == player) return 2*3 +0;      // [2][0]

        var i : Int = 0;
        while(i < 3)
        {
            //Horitzontals
            if(table[i][0] == 0 && table[i][1] == table[i][2] && table[i][1] == player) return i*3 +0;  // [i][0]
            if(table[i][1] == 0 && table[i][0] == table[i][2] && table[i][2] == player) return i*3 +1;  // [i][1]
            if(table[i][2] == 0 && table[i][0] == table[i][1] && table[i][0] == player) return i*3 +2;  // [i][2]

            //Verticals
            if(table[0][i] == 0 && table[1][i] == table[2][i] && table[1][i] == player) return 0*3 +i;  // [0][i]
            if(table[1][i] == 0 && table[0][i] == table[2][i] && table[2][i] == player) return 1*3 +i;  // [1][i]
            if(table[2][i] == 0 && table[0][i] == table[1][i] && table[0][i] == player) return 2*3 +i;  // [2][i]

            i++;
        }

        return -1; // No hi ha cap moviment bo
    }

    /**
     *  Dibuixa a una posició
     *  @param player Jugador.  1 per usuari, 2 per màquina
     */
    fun pressPos(player:Int, posX:Int, posY:Int)
    {
        val posBtn:ImageButton = bts[3*posX +posY];
        if(table[posX][posY] != 0) // No s'hauria de donar el cas
        {
            Log.e("ERR", "Invalid position [$posX][$posY]: value is ${table[posX][posY]}");
            return;
        }
        table[posX][posY] = player;
        playerTurn = !playerTurn;

        posBtn.setImageResource(if(player == 1) R.drawable.ter_x  else R.drawable.ter_o);
        posBtn.isEnabled = false;

        changeTurn();
    }

    /**
     *  Comprova si s'ha acabat el joc.
     *  @return 0 si no s'ha acabat el joc, -1 si s'ha acabat empatat o la ID del guanyador (1, 2)
     */
    private fun checkEnd(): Int
    {
        if ( (table[0][0] == table[1][1] && table[1][1] == table[2][2]  //Diagonal \
          ||  table[0][2] == table[1][1] && table[1][1] == table[2][0]) //Diagonal /
          && table[1][1] != 0)
        {
            return table[1][1];
        }

        //Horitzontal i vertical
        var i:Int = 0;
        while(i < 3)
        {
            if ( (table[i][0] == table[i][1] && table[i][1] == table[i][2]  //Horitzontal
              ||  table[0][i] == table[1][i] && table[1][i] == table[2][i]) //Vertical
              && table[i][i] != 0)
            {
                return table[i][i];
            }
            i++;
        }

        for (table2 in table)
        {
            for(pos in table2)
            {
                if(pos == 0) //Encara es poden fer jugades
                    return 0;
            }
        }

        return -1; // Empat
    }

    /**
     *  Final del joc. Puja els resultats a la BBDD.
     *  @param winner 1 per jugador, 2 per màquina, -1 en empat
     */
    private fun endGame(winner: Int)
    {
        if(winner > 0)
            Toast.makeText(this, "El guanyador és $winner!", Toast.LENGTH_LONG).show();

        for (button in bts)
            button.isEnabled = false;

        //TODO: add win/loss to DB

        var auth = FirebaseAuth.getInstance();
        var user:FirebaseUser? = auth.currentUser;

        var uid:String= user!!.uid;
        var dbPlays:Int=1;
        var dbWins:Int=0;
        var dbLoss:Int=0;
        var found:Boolean = false;


        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        val dbreference: DatabaseReference = database.getReference("TRES EN RATLLA");
        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {
                    if (ds.key.toString() == uid) {

                        dbPlays += ds.child("TotalPlays").value.toString().toInt();
                        dbWins = ds.child("Wins").value.toString().toInt();
                        dbLoss = ds.child("Losses").value.toString().toInt();
                        Log.i("STATS1", "$dbPlays");
                        Log.i("STATS1", "$dbWins");
                        Log.i("STATS1", "$dbLoss");

                        found=true;
                        break;
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", "ERROR DATABASE CANCEL");
            }
        });

        if     (winner == 1) dbWins++;
        else if(winner == 2) dbLoss++;
        Log.i("STATS2", "$dbPlays");
        Log.i("STATS2", "$dbWins");
        Log.i("STATS2", "$dbLoss");
        updateDB(dbreference, uid, dbPlays, dbWins, dbLoss);

    }

    fun updateDB(db:DatabaseReference, uid:String, plays:Int, wins:Int, losses:Int)
    {
        val playerData : HashMap<String,Any> = HashMap<String, Any>();
        playerData["TotalPlays"] = plays;
        playerData["Wins"] = wins;
        playerData["Losses"] = losses;
        db.child(uid).setValue(playerData);
        Log.i("DB", "Successfully updated user statistics");
    }


}