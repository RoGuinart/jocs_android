package com.example.jocs_guinart.Menus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import com.example.jocs_guinart.Games.TresEnRatlla
import com.example.jocs_guinart.R
import java.util.*

class TresEnRatllaMenu : AppCompatActivity() {

    private lateinit var plr : RadioButton;
    private lateinit var mch : RadioButton;
    private lateinit var rnd : RadioButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tres_en_ratlla_menu);

        val start: Button = findViewById(R.id.start);
        val back : Button = findViewById(R.id.back);

        back.setOnClickListener(){
            val intent= Intent(this, GameMenu::class.java);
            startActivity(intent);
            finish();
        }

        start.setOnClickListener(){
            val intent= Intent(this, TresEnRatlla::class.java);
            intent.putExtra("playerFirst", getChoice());
            intent.putExtra("UID", "");
            intent.putExtra("TOT_GAMES", 0);
            intent.putExtra("WINS", 0);
            intent.putExtra("LOSSES", 0);
            startActivity(intent);
            finish();
        }

        plr = findViewById(R.id.playerB);
        mch = findViewById(R.id.machineB);
        rnd = findViewById(R.id.randB);

        plr.isChecked = true; //Opció per defecte

        plr.setOnClickListener() { check(1); }
        mch.setOnClickListener() { check(2); }
        rnd.setOnClickListener() { check(3); }
    }

    private fun check(choice: Int)
    {
        plr.isChecked = choice == 1;
        mch.isChecked = choice == 2;
        rnd.isChecked = choice == 3;
    }

    private fun getChoice(): Boolean
    {
        if(plr.isChecked) return true;
        if(mch.isChecked) return false;
        if(rnd.isChecked) return Random().nextBoolean();
        return true; // Cas impossible - però si es donés, el jugador començaria primer.
    }
}