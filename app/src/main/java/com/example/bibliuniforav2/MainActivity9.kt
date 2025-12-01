package com.example.bibliuniforav2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity9 : AppCompatActivity() {

    lateinit var etNome: EditText
    lateinit var etMatricula: EditText
    lateinit var btnsalvar: Button
    lateinit var fb: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main9)

        val backButton = findViewById<ImageButton>(R.id.imageButton3)
        backButton.setOnClickListener { finish() }

        etNome = findViewById(R.id.editTextnome)
        etMatricula = findViewById(R.id.editTextmatricula)
        btnsalvar = findViewById(R.id.button4)

        fb = Firebase.firestore
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        btnsalvar.setOnClickListener {
            atualizarDados()
        }
    }

    private fun atualizarDados() {

        val uid = auth.currentUser?.uid

        // VERIFICA SE O USUÁRIO ESTÁ LOGADO
        if (uid == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        fb.collection("usuarios")
            .document(uid)
            .update(
                mapOf(
                    "nome" to etNome.text.toString(),
                    "matricula" to etMatricula.text.toString(),
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show()
            }
    }
}

