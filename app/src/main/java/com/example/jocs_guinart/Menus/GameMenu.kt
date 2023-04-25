package com.example.jocs_guinart.Menus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.example.jocs_guinart.R

class GameMenu : AppCompatActivity() {
    private var NOM: String =""
    private var PUNTUACIO: String=""
    private var UID: String=""
    private var NIVELL: String=""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        val penjat: ImageButton = findViewById(R.id.penjat)
        val tresenratlla : ImageButton= findViewById(R.id.tresenratlla)
        val reversi: ImageButton = findViewById(R.id.reversi)

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
            val intent= Intent(this, OthelloMenu::class.java);
            startActivity(intent);
            finish();
        }
    }
}