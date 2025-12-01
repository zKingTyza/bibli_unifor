package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity8 : AppCompatActivity() {

    lateinit var vNome: TextView
    lateinit var vMatricula: TextView
    lateinit var vEmail: TextView
    lateinit var vSenha: TextView
    lateinit var btnat: Button
    lateinit var fb: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main8)

        vNome = findViewById(R.id.textView13)
        vMatricula = findViewById(R.id.textView17)
        vEmail = findViewById(R.id.textView15)



        fb = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Voltar
        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            finish()
        }

        setupBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        coletaDados()
    }

    private fun coletaDados() {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            vNome.text = "Usuário não logado"
            return
        }

        fb.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { result ->

                vNome.text = result.getString("nome")
                vEmail.text = result.getString("email")
                vMatricula.text = result.getString("matricula")
            }
            .addOnFailureListener {
                vNome.text = "Erro ao carregar dados"
            }
    }


    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {}
                R.id.nav_library -> startActivity(Intent(this, MainActivity31::class.java))
                R.id.nav_home -> startActivity(Intent(this, MainActivity4::class.java))
                R.id.nav_donate -> startActivity(Intent(this, MainActivity32::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, MainActivity7::class.java))
            }
            true
        }

        bottomNavigationView.setOnItemReselectedListener {}
    }
}

