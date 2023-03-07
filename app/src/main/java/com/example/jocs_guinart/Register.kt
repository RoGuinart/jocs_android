package com.example.jocs_guinart

import android.content.Intent
import android.icu.text.DateFormat.getDateInstance
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    //Definim les variables que farem servir
    //lateinit ens permet no inicialitzar-les encara
    lateinit var correoEt  : EditText;
    lateinit var passEt    : EditText;
    lateinit var nombreEt  : EditText;
    lateinit var fechaTxt  : TextView;
    lateinit var Registrar : Button;
    lateinit var auth: FirebaseAuth //FIREBASE AUTENTIFICACIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        activity_setup();
    }

    private fun activity_setup() {
        correoEt  = findViewById<EditText>(R.id.EMail);
        passEt    = findViewById<EditText>(R.id.PWD);
        nombreEt  = findViewById<EditText>(R.id.USR);
        fechaTxt  = findViewById<TextView>(R.id.date);
        Registrar = findViewById<Button>  (R.id.Register);

        //Carreguem la data al TextView
        //Utilitzem calendar (hi ha moltes altres opcions)
        val date = Calendar.getInstance().time;
        val formatter = SimpleDateFormat.getDateInstance(); //or use getDateInstance()
        val formatedDate = formatter.format(date);
        //ara la mostrem al TextView
        fechaTxt.text = formatedDate;

        auth = FirebaseAuth.getInstance();

        Registrar.setOnClickListener()
        {
            //Abans de fer el registre validem les dades;
            var email: String = correoEt.text.toString();
            var pass:  String = passEt.text.toString();
            // validació del correu
            // si no es de tipus correu
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                correoEt.error = "Invalid Mail";
            } else if (pass.length < 6) {
                passEt.error = "Password less than 6 chars";
            } else {
                RegistrarJugador(email, pass);
            }
        }
    }

    private fun RegistrarJugador(email:String, passw:String){
        auth.createUserWithEmailAndPassword(email, passw).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(this,"User created successfully.", Toast.LENGTH_SHORT).show();
                val user = auth.currentUser;
                updateUI(user);
            } else {
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun updateUI(user: FirebaseUser?){
        if (user!=null)
            saveToDB(user);
        else
            Toast.makeText( this,"Error creating user",Toast.LENGTH_SHORT).show()
    }

    private fun saveToDB(user: FirebaseUser)
    {
        var dadesJugador : HashMap<String,Any> = HashMap<String, Any>();
        dadesJugador.put ("Uid",        user.uid);
        dadesJugador.put ("Email",      correoEt.text.toString());
        dadesJugador.put ("Password",   passEt.text.toString()); //TODO: encriptar
        dadesJugador.put ("Nom",        nombreEt.text.toString());
        dadesJugador.put ("Data",       fechaTxt.text.toString());
        dadesJugador.put ("Puntuacio",  0);

        // Creem un punter a la base de dades i li donem un nom
        var database:  FirebaseDatabase  = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        var reference: DatabaseReference = database.getReference("DATA BASE JUGADORS");

        if(reference!=null)
        {
            //crea un fill amb els valors de dadesJugador
            reference.child(user.uid).setValue(dadesJugador);
            Toast.makeText(this, "Registrat amb èxit", Toast.LENGTH_SHORT).show();
            val mainMenu = Intent(this, MainMenu::class.java);
            startActivity(mainMenu);
        }
        else
        {
            Toast.makeText(this, "ERROR DB", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}