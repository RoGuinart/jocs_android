package com.example.jocs_guinart

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


class MainMenu : AppCompatActivity() {

    lateinit var auth: FirebaseAuth;
    var user: FirebaseUser? = null;

    lateinit var tancarSessio: Button;
    lateinit var CreditsBtn: Button;
    lateinit var PuntuacionsBtn: Button;
    lateinit var jugarBtn: Button;
    lateinit var editarBtn: Button;

    lateinit var miPuntuaciotxt: TextView;
    lateinit var puntuacio: TextView;
    lateinit var uid: TextView;
    lateinit var correo: TextView;
    lateinit var nom: TextView;
    lateinit var edat: TextView;
    lateinit var poblacio: TextView;
    lateinit var imatgePerfil: ImageView;

    lateinit var imatgeUri: Uri
    lateinit var storageReference: StorageReference;

    //reference serà el punter que ens envia a la base de dades de jugadors
    lateinit var reference: DatabaseReference;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        auth = FirebaseAuth.getInstance();
        user = auth.currentUser;
        storageReference = FirebaseStorage.getInstance().reference;

        val tf = Typeface.createFromAsset(assets, "fonts/DSCaslonGotisch.ttf");

        tancarSessio = findViewById(R.id.tancarSessio);
        CreditsBtn = findViewById(R.id.CreditsBtn);
        PuntuacionsBtn = findViewById(R.id.PuntuacionsBtn);
        jugarBtn = findViewById(R.id.jugarBtn);
        editarBtn = findViewById(R.id.editarBtn);

        tancarSessio.typeface = tf;
        CreditsBtn.typeface = tf;
        PuntuacionsBtn.typeface = tf;
        jugarBtn.typeface = tf;
        editarBtn.typeface = tf;



        miPuntuaciotxt = findViewById(R.id.miPuntuaciotxt);
        puntuacio = findViewById(R.id.puntuacio);
        uid = findViewById(R.id.uid);
        correo = findViewById(R.id.correo);
        nom = findViewById(R.id.nom);
        edat = findViewById(R.id.edat);
        poblacio = findViewById(R.id.poblacio);
        imatgePerfil = findViewById(R.id.alienimagen);

        miPuntuaciotxt.typeface = tf;
        puntuacio.typeface = tf;
        uid.typeface = tf;
        correo.typeface = tf;
        nom.typeface = tf;
        edat.typeface = tf;
        poblacio.typeface = tf;


        consulta();

        jugarBtn.setOnClickListener() {
            startActivity(Intent(this, GameMenu::class.java));
        }
        CreditsBtn.setOnClickListener() {
            Toast.makeText(this, "Credits", Toast.LENGTH_SHORT).show();
        }
        PuntuacionsBtn.setOnClickListener() {
            Toast.makeText(this, "Puntuacions", Toast.LENGTH_SHORT).show();
        }
        editarBtn.setOnClickListener() {
            canviaLaImatge();
        }
        tancarSessio.setOnClickListener() {
            SaveSharedPreference.clearUserName(this);
            startActivity(Intent(this, MainActivity::class.java));
            finish();
        }

    }

    // Aquest mètode s'executarà quan s'obri el minijoc
    override fun onStart() {
        userLogged();
        super.onStart();
    }

    private fun userLogged() {
        if (user != null) {
            Toast.makeText(this, "Jugador logejat", Toast.LENGTH_SHORT).show();
        } else {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }
    }

    private fun consulta() {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://projecte-m8-default-rtdb.europe-west1.firebasedatabase.app/");
        val bdreference: DatabaseReference = database.getReference("DATA BASE JUGADORS");
        bdreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // ara capturem tots els fills
                var trobat: Boolean = false
                for (ds in snapshot.children) {
                    //mirem si el mail és el mateix que el del jugador
                    //si és així, mostrem les dades als textview  corresponents
                    if (ds.child("Email").value.toString() == user?.email) {
                        trobat = true

                        //carrega els textview
                        puntuacio.text = ds.child("Puntuacio").value.toString();
                        uid.text = ds.child("Uid").value.toString();
                        correo.text = ds.child("Email").value.toString();
                        nom.text = ds.child("Nom").value.toString();
                        poblacio.text = ds.child("Poblacio").value.toString()
                        edat.text = ds.child("Edat").value.toString()


                        // Imatge perfil

                        val folderReference: StorageReference = storageReference.child("FotosPerfil");
                        val MB: Long = 1024*1024;

                        folderReference.child(uid.text.toString()).getBytes(MB)
                            .addOnSuccessListener(OnSuccessListener<ByteArray> { bytes ->
                                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imatgePerfil.setImageBitmap(bmp);
                            }).addOnFailureListener(OnFailureListener {
                                imatgePerfil.setImageResource(R.drawable.def_player);
                            });

                        return;
                    }

                    Log.e("ERROR", "ERROR NO TROBAT MAIL");
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", "ERROR DATABASE CANCEL");
            }
        });

        //Imatge perfil

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val REQUEST_CODE = 201
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if(data?.data != null)
                imatgeUri = data.data!!;

            imatgePerfil.setImageURI(imatgeUri);
            pujarFoto(imatgeUri);

        } else {
            Toast.makeText(this, "Error recuperant imatge de galeria", Toast.LENGTH_SHORT).show()
        }
    }

    private fun canviaLaImatge() {
        // utilitzarem un alertdialog que seleccionara de galeria o agafar una foto
        // Si volem fer un AlertDialog amb més de dos elements (amb una llista),
        // Aixó ho fariem amb fragments (que veurem més endevant)
        // Aquí hi ha un tutorial per veure com es fa:
        // https://www.codevscolor.com/android-kotlin-list-alert-dialog
        // Veiem com es crea un de dues opcions (habitualment acceptar o cancel·lar:
        val dialog = AlertDialog.Builder(this)
            .setTitle("CANVIAR IMATGE")
            .setMessage("Seleccionar imatge de: ")
            .setNegativeButton("Galeria") { view, _ ->
                Toast.makeText(
                    this, "De galeria",
                    Toast.LENGTH_SHORT
                ).show()
                //mirem primer si tenim permisos per a accedir a Read External Storage
                if (askForPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    val intent = Intent(Intent.ACTION_PICK)
                    val REQUEST_CODE = 201 //Aquest codi és un número que farem servir per
                    // a identificar el que hem recuperat del intent
                    // pot ser qualsevol número
                    intent.type = "image/*";
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(
                        this, "ERROR PERMISOS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setPositiveButton("Càmera") { view, _ ->
                Toast.makeText( this, "A IMPLEMENTAR PELS ALUMNES", Toast.LENGTH_LONG).show()
                if (askForPermissions(android.Manifest.permission.CAMERA)) {
                    //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    val REQUEST_CODE = 201 //Aquest codi és un número que farem servir per
                    // a identificar el que hem recuperat del intent
                    // pot ser qualsevol número
                    //intent.type = "image/*";

                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "photo")
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
                    imatgeUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )!!
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imatgeUri)
                    startActivityForResult(intent, REQUEST_CODE)

                } else {
                    Toast.makeText(
                        this, "ERROR PERMISOS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                view.dismiss()
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun pujarFoto(imatgeUri: Uri) {
        val folderReference: StorageReference = storageReference.child("FotosPerfil");
        val Uids: String = uid.text.toString();
        //Podriem fer:
        //folderReference.child(Uids).putFile(imatgeUri)
        //Pero utilitzem el mètode recomanat a la documentació
        // https://firebase.google.com/docs/storage/android/upload- files
        // Get the data from an ImageView as bytes
        imatgePerfil.isDrawingCacheEnabled = true
        imatgePerfil.buildDrawingCache()
        val bitmap = (imatgePerfil.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = folderReference.child(Uids).putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Error enviant imatge a Storage", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->

            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    //----------------------------------------Permisos----------------
    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun askForPermissions(perm : String): Boolean {
        val REQUEST_CODE = 201
        if (!isPermissionsAllowed()) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,perm))
                showPermissionDeniedDialog();
            else
                ActivityCompat.requestPermissions(this, arrayOf(perm), REQUEST_CODE);
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult
    (
        requestCode : Int,
        permissions: Array<String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
        val REQUEST_CODE = 201
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else { // permission is denied, you can ask for permission again, if you want
                    // askForPermissions()
                }
                return
            }
        }
    }

    fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
                    val uri = Uri.fromParts("package", packageName, null);
                    intent.data = uri;
                    startActivity(intent);
                }).setNegativeButton("Cancel", null).show();
    }
//----------------------------------------------------------------
}