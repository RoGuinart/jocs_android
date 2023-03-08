package com.example.jocs_guinart

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RawRes

class Splash : AppCompatActivity() {
    private val duracio: Long = 1000;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        supportActionBar?.hide();
        //var sound: MediaPlayer = MediaPlayer.create(context, ) // TODO: afegir so
        canviarActivity();
    }

    private fun canviarActivity() {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }, duracio);
    }
}