package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {

    // Campos do formulário
    lateinit var etNome: EditText
    lateinit var etMatricula: EditText
    lateinit var etEmail: EditText
    lateinit var etSenha: EditText
    lateinit var btnSalvar: Button
    lateinit var toggleAdmin: ToggleButton


    // Firebase
    lateinit var fb: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Inicialização de componentes
        etNome = findViewById(R.id.editTextText2)
        etMatricula = findViewById(R.id.editTextNumberPassword)
        etEmail = findViewById(R.id.editTextTextEmailAddress2)
        etSenha = findViewById(R.id.editTextTextPassword3)
        btnSalvar = findViewById(R.id.button2)
        toggleAdmin = findViewById(R.id.toggleAdmin)


        fb = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Botão de confirmação
        btnSalvar.setOnClickListener {
            Toast.makeText(this, "Cadastro Confirmado", Toast.LENGTH_SHORT).show()
            adicionarDados()
        }

        // Botão de voltar
        val botaoVoltar: ImageButton = findViewById(R.id.imageButton)
        botaoVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Layout padrão
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun adicionarDados() {

        val email = etEmail.text.toString()
        val senha = etSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar usuário no Firebase Auth
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d("cadastro", "createUserWithEmail:success")

                    val uid = task.result?.user?.uid

                    if (uid == null) {
                        Toast.makeText(this, "Erro ao obter UID!", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Salvar dados no Firestore usando o UID como ID do documento
                    val dadosUsuario = mapOf(
                        "nome" to etNome.text.toString(),
                        "matricula" to etMatricula.text.toString(),
                        "email" to etEmail.text.toString(),
                        "uidAuth" to uid,
                        "admin" to toggleAdmin.isChecked   // <-- ADICIONADO
                    )


                    fb.collection("usuarios")
                        .document(uid)
                        .set(dadosUsuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao salvar no banco: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {

                    Log.w("cadastro", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Erro ao criar conta: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
    }
}
