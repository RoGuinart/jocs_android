package com.example.jocs_guinart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class GameMenu : AppCompatActivity() {
    private var NOM: String =""
    private var PUNTUACIO: String=""
    private var UID: String=""
    private var NIVELL: String=""

    //lateinit var penjat : ImageButton;
    //lateinit var tresenratlla : ImageButton;
    //lateinit var reversi : ImageButton;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        var penjat: ImageButton = findViewById(R.id.penjat)
        var tresenratlla : ImageButton= findViewById(R.id.tresenratlla)
        var reversi: ImageButton = findViewById(R.id.reversi)

        penjat.setOnClickListener(){
            Toast.makeText(this,"penjat", Toast.LENGTH_SHORT).show();
        }
        tresenratlla.setOnClickListener(){
//hem d'enviar el id, el nom i el contador, i el nivell
            val intent= Intent(this, TresEnRatllaMenu::class.java);
            startActivity(intent);
            finish();
        }
        reversi.setOnClickListener(){
            Toast.makeText(this,"Reversi", Toast.LENGTH_SHORT).show();
        }
    }
}