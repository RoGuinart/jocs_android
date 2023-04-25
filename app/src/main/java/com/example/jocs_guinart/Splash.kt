package com.example.jocs_guinart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.jocs_guinart.Menus.MainMenu


object SaveSharedPreference {
    private const val PREF_USER_NAME = "username"
    private fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun setUserName(ctx: Context?, userName: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_NAME, userName)
        editor.apply();
    }

    fun getUserName(ctx: Context?): String? {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "")
    }

    fun clearUserName(ctx: Context?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.clear() //clear all stored data
        editor.apply()
    }
}
class Splash : AppCompatActivity() {
    private val len: Long = 1000; //ms
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        supportActionBar?.hide();
        //var sound: MediaPlayer = MediaPlayer.create(context, ) // TODO: afegir so
        canviarActivity();
    }

    private fun canviarActivity() {
        Handler().postDelayed({
            if(SaveSharedPreference.getUserName(this)?.isEmpty() == true)
            {
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish();
            }
            else
            {
                val intent = Intent(this, MainMenu::class.java);
                startActivity(intent);
                finish();
            }
        }, len);
    }
}