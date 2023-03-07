package com.example.jocs_guinart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity()
{
    //Despleguem les variables que farem servir
    lateinit var correoLogin: EditText;
    lateinit var passLogin: EditText;
    lateinit var BtnLogin: Button;
    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Busquem a R els elements als que apunten les variables
        correoLogin = findViewById<EditText>(R.id.correoLogin);
        passLogin = findViewById<EditText>(R.id.passLogin);
        BtnLogin = findViewById<Button>(R.id.LoginBtn);
        auth = FirebaseAuth.getInstance()


        BtnLogin.setOnClickListener()
        {
            //Abans de fer el registre validem les dades
            var email: String = correoLogin.text.toString();
            var passw: String = passLogin.text.toString();
            // validació del correu
            // si no es de tipus correu
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                correoLogin.error = "Invalid Mail";
            } else if (passw.length < 6)
            {
                passLogin.error = "Password less than 6 chars";
            } else
            {
                login(email, passw);
            }
        }
    }

    private fun login(email: String, passw: String)
    {
        auth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {
                val tx: String = "Benvingut " + email;
                Toast.makeText(this, tx, Toast.LENGTH_LONG).show();
                val user = auth.currentUser;
                updateUI(user);
            } else {
                Toast.makeText(this, "ERROR Autentificació", Toast.LENGTH_LONG).show();
            }
        }
    }

    fun updateUI(user: FirebaseUser?)
    {
        startActivity(Intent(this, MainMenu::class.java));
        finish();
    }
}