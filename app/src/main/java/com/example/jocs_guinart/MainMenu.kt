package com.example.jocs_guinart

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.security.AccessController.getContext

class MainMenu : AppCompatActivity() {

    lateinit var auth: FirebaseAuth;
    var user:FirebaseUser? = null;

    lateinit var tancarSessio:   Button;
    lateinit var CreditsBtn:     Button;
    lateinit var PuntuacionsBtn: Button;
    lateinit var jugarBtn:       Button;

    lateinit var miPuntuaciotxt: TextView;
    lateinit var puntuacio:      TextView;
    lateinit var uid:            TextView;
    lateinit var correo:         TextView;
    lateinit var nom:            TextView;

    //reference serà el punter que ens envia a la base de dades de jugadors
    lateinit var reference: DatabaseReference;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        auth = FirebaseAuth.getInstance();
        user = auth.currentUser;

        tancarSessio = findViewById(R.id.tancarSessio);
        CreditsBtn =  findViewById(R.id.CreditsBtn);
        PuntuacionsBtn = findViewById(R.id.PuntuacionsBtn);
        jugarBtn = findViewById(R.id.jugarBtn);

        val tf = Typeface.createFromAsset(assets,"fonts/DSCaslonGotisch.ttf");

        tancarSessio.typeface   = tf;
        CreditsBtn.typeface     = tf;
        PuntuacionsBtn.typeface = tf;
        jugarBtn.typeface       = tf;

        miPuntuaciotxt=findViewById(R.id.miPuntuaciotxt);
        puntuacio=findViewById(R.id.puntuacio);
        uid=findViewById(R.id.uid);
        correo=findViewById(R.id.correo);
        nom=findViewById(R.id.nom);

        miPuntuaciotxt.typeface = tf;
        puntuacio.typeface      = tf;
        uid.typeface            = tf;
        correo.typeface         = tf;
        nom.typeface            = tf;

//fem el mateix amb el text dels botons
        tancarSessio.typeface = tf;
        CreditsBtn.typeface = tf;
        PuntuacionsBtn.typeface = tf;
        jugarBtn.typeface = tf;

        consulta();

        jugarBtn.setOnClickListener(){
            startActivity(Intent(this, GameMenu::class.java));
        }
        CreditsBtn.setOnClickListener(){
            Toast.makeText(this,"Credits", Toast.LENGTH_SHORT).show();
        }
        PuntuacionsBtn.setOnClickListener(){
            Toast.makeText(this,"Puntuacions", Toast.LENGTH_SHORT).show();
        }
        tancarSessio.setOnClickListener(){
            Toast.makeText(this,"Log out", Toast.LENGTH_SHORT).show();
        }

    }

    // Aquest mètode s'executarà quan s'obri el minijoc
    override fun onStart() {
        userLogged();
        super.onStart();
    }

    private fun userLogged()
    {
        if(user!=null)
        {
            Toast.makeText(this, "Jugador logejat", Toast.LENGTH_SHORT).show();
        } else
        {
            val intent= Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }
    }

    private fun consulta()
    {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        var bdreference:DatabaseReference = database.getReference("DATA BASE JUGADORS");
        bdreference.addValueEventListener (object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i ("DEBUG","arrel value: "+ snapshot.value.toString());
                Log.i ("DEBUG","arrel key: "+ snapshot.key.toString());
                // ara capturem tots els fills
                var trobat:Boolean =false
                for (ds in snapshot.children) {
                    Log.i ("DEBUG","DS key: "   +ds.child("Uid").key.toString());
                    Log.i ("DEBUG","DS value: " +ds.child("Uid").value.toString());
                    Log.i ("DEBUG","DS data: "  +ds.child("Data").value.toString());
                    Log.i ("DEBUG","DS mail: "  +ds.child("Email").value.toString());
                    //mirem si el mail és el mateix que el del jugador
                    //si és així, mostrem les dades als textview  corresponents
                    if(ds.child("Email").value.toString() == user?.email)
                    {
                        trobat=true

                        //carrega els textview
                        puntuacio.text = ds.child("Puntuacio").value.toString();
                        uid.text = ds.child("Uid").value.toString();
                        correo.text = ds.child("Email").value.toString();
                        nom.text = ds.child("Nom").value.toString();
                    }
                    if (!trobat)
                    {
                        Log.e ("ERROR","ERROR NO TROBAT MAIL");
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e ("ERROR","ERROR DATABASE CANCEL");
            }
        })
    }
}
