package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Componentes
        val textoCadastro: TextView = findViewById(R.id.textViewRegister)
        val textoEsqueciSenha: TextView = findViewById(R.id.textViewForgotPasswordLink)
        val botaoAcessar: Button = findViewById(R.id.button)

        val campoEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
        val campoSenha: EditText = findViewById(R.id.editTextTextPassword)

        // Ir para tela de cadastro
        textoCadastro.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

        // Ir para tela de recuperação
        textoEsqueciSenha.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        // Login real com Firebase
// Login real com Firebase
        botaoAcessar.setOnClickListener {

            val email = campoEmail.text.toString().trim()
            val senha = campoSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid

                        if (uid == null) {
                            Toast.makeText(this, "Erro ao obter UID!", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        // 🔥 AGORA BUSCA O USUÁRIO NO FIRESTORE
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { doc ->

                                val isAdmin = doc.getBoolean("admin") ?: false

                                if (isAdmin) {
                                    // 🔥 SE FOR ADMIN
                                    Toast.makeText(
                                        this,
                                        "Bem-vindo, Administrador!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, MainActivity20::class.java))
                                    finish()
                                } else {
                                    // 🔥 SE FOR USUÁRIO NORMAL
                                    Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT)
                                        .show()
                                    startActivity(Intent(this, MainActivity4::class.java))
                                    finish()
                                }

                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Erro ao buscar dados do usuário!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {

                        Toast.makeText(
                            this,
                            "Erro ao acessar: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}


