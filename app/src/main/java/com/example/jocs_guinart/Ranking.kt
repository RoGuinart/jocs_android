package com.example.jocs_guinart

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jocs_guinart.Recycler.RankingAdapter
import com.example.jocs_guinart.Recycler.RankingPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.sync.Semaphore

class Ranking : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth;
    private lateinit var db : FirebaseDatabase;
    private lateinit var database: DatabaseReference;
    private lateinit var storageRef: StorageReference;
    private lateinit var recyclerView : RecyclerView;

    private lateinit var spinner : Spinner;

    val players = ArrayList<RankingPlayer>();
    var gameStr : String = "";
    var rankingType : Boolean = false; // false = win ratio, true = total wins

    var totalPlayers : Long = 0;
    var curPlayerCount : Long = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        db = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        auth = FirebaseAuth.getInstance();
        database = db.getReference("Tres en ratlla"); // Tres en ratlla per defecte
        storageRef = FirebaseStorage.getInstance().reference;


        findViewById<Button>(R.id.bBack).setOnClickListener {
            finish();
        }
        val bSwap =  findViewById<Button>(R.id.bSwap);
        bSwap.setOnClickListener {
            if(rankingType) bSwap.text = "Rank by total wins"
            else            bSwap.text = "Rank by win ratio"

            rankingType = !rankingType
            checkRanking();
        }

        recyclerViewInit();
    }





    fun recyclerViewInit()
    {
        recyclerView = findViewById<RecyclerView>(R.id.recycler);
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = RankingAdapter(ArrayList());

        // Botó de llistat de jocs
        spinner = findViewById(R.id.rankingGames);
        ArrayAdapter.createFromResource(this, R.array.games,android.R.layout.simple_spinner_item)
        .also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.adapter = adapter;
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                val selectedItem : GAMES = when(parent!!.getItemAtPosition(position).toString())
                {
                    "Tres en ratlla"    -> GAMES.TRES_EN_RATLLA;
                    "Othello"           -> GAMES.OTHELLO;
                    "Penjat"            -> GAMES.PENJAT;
                    else                -> GAMES.TRES_EN_RATLLA; // Op
                }
                recyclerDBInit(selectedItem);
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("RANK", "Spinner nothing");
            }
        }
    }

    fun recyclerDBInit(game_enum : GAMES) //: List<RankingPlayer>
    {
        //val list : List<RankingPlayer> = emptyList();
        val newGameStr : String = when(game_enum)
        {
            GAMES.TRES_EN_RATLLA -> "TRES EN RATLLA";
            GAMES.OTHELLO -> "OTHELLO";
            GAMES.PENJAT -> "PENJAT";   // no l'he fet
            GAMES.DAMES -> "DAMES";     // no l'he fet
        };
        if(newGameStr == gameStr)
            return; //No hem de fer res

        gameStr = newGameStr;
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        val dbRef : DatabaseReference = database.reference;

        // Esborrem els elements de la llista, ja que seran diferents.
        players.removeAll(players.toSet());
        totalPlayers = 0;
        curPlayerCount = 0;


        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalPlayers = snapshot.child(gameStr).childrenCount;
                val auth : FirebaseAuth = FirebaseAuth.getInstance();
                val tables = snapshot.child(gameStr);
                Log.i("DB", "!${tables.key.toString()}");
                for(player in snapshot.child(gameStr).children)
                {
                    Log.i("DasdB", "${player.key.toString()} from ${snapshot.child(gameStr).children}");
                    val uid : String = player.key.toString();

                    val games = player.child("TotalGames").value.toString().toInt();
                    val wins = player.child("Wins").value.toString().toInt();
                    val losses = player.child("Losses").value.toString().toInt();
                    val username = snapshot.child("DATA BASE JUGADORS/").child(player.key.toString()).child("Nom").value.toString();
                    Log.i("TEST", username);

                    storageRef.child("FotosPerfil/$uid").downloadUrl.addOnSuccessListener { uri ->
                        val plyr: RankingPlayer = RankingPlayer(uid, username, games, wins, losses, uri);
                        players.add(plyr);
                        checkRanking();
                    }.addOnFailureListener {
                        val plyr: RankingPlayer = RankingPlayer(uid, username, games, wins, losses, null);
                        players.add(plyr);
                        checkRanking();
                    }
                }

                recyclerView.adapter = RankingAdapter(players); // Per si de cas no hi ha ningú al ranking, que es borrin a l'aplicació.
                                                                // Si hi ha algú, es sobreescriurà més tard. No queda malament.
                                                                // Si per qualsevol raó es cridés després de carregar tots els jugadors,
                                                                // el funcionament és el mateix; no hi ha cap problema.
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RANK", "DATABASE ERROR: $error");
            }
        });
    }
    private fun checkRanking()
    {
        curPlayerCount++
        if(curPlayerCount >= totalPlayers) {

            if(rankingType)
                players.sortWith(compareByDescending<RankingPlayer> {  it.wonGames.toFloat() / it.totGames });
            else
                players.sortWith(compareByDescending<RankingPlayer> { it.wonGames });
            recyclerView.adapter = RankingAdapter(players);
        }
    }
}