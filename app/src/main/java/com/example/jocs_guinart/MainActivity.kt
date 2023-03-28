package com.example.jocs_guinart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val BTMLOGIN = findViewById<Button>(R.id.BTMLOGIN);
        val BTMREGISTRO = findViewById<Button>(R.id.BTMREGISTRO);
        BTMLOGIN.setOnClickListener(){
            startActivity(Intent(this, Login::class.java));
            finish();
        }
        BTMREGISTRO.setOnClickListener(){
            startActivity(Intent(this, Register::class.java));
            finish();
        }
    }

}