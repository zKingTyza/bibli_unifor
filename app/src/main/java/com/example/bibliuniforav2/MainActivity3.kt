package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity3 : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        // --- Código para o Toast ---
        val botaoAlterar: Button = findViewById(R.id.button3)

        botaoAlterar.setOnClickListener {

            val email = findViewById<EditText>(R.id.editTextTextPassword2).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "E-mail de redefinição enviado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Erro ao enviar e-mail",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            finish()
        }



        // --- Código para Navegação ---
        val botaoVoltar: ImageButton = findViewById(R.id.imageButton2)

        botaoVoltar.setOnClickListener {
            // *** CORREÇÃO ***
            // Usamos o nome da classe Kotlin (MainActivity), não o nome do arquivo XML (activity_main).
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // --- Código Padrão do Layout ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
