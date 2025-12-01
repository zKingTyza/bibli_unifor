package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity16 : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editMatricula: EditText
    private lateinit var editCpf: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPerfil: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnVoltar: Button

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("usuarios")
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main16)

        initViews()
        getUserIdFromIntent()
        loadUserData()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun initViews() {
        editNome = findViewById(R.id.editNome)
        editMatricula = findViewById(R.id.editMatricula)
        editCpf = findViewById(R.id.editCpf)
        editEmail = findViewById(R.id.editEmail)
        editPerfil = findViewById(R.id.editPerfil)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnVoltar = findViewById(R.id.btnVoltar)
    }

    private fun getUserIdFromIntent() {
        userId = intent.getStringExtra("USER_ID") ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "Erro: ID do usuário não encontrado", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadUserData() {
        if (userId.isNotEmpty()) {
            usersCollection.document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val usuario = document.toObject(Usuario::class.java)
                        usuario?.let {
                            editNome.setText(it.nome)
                            editMatricula.setText(it.matricula)
                            editCpf.setText(it.cpf)
                            editEmail.setText(it.email)
                            editPerfil.setText(it.perfil)
                        }
                    } else {
                        Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao carregar usuário: ${exception.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
        }
    }

    private fun setupClickListeners() {
        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }

        btnVoltar.setOnClickListener {
            voltarParaMain15()
        }
    }

    private fun salvarAlteracoes() {
        val nome = editNome.text.toString().trim()
        val matricula = editMatricula.text.toString().trim()
        val cpf = editCpf.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val perfil = editPerfil.text.toString().trim()

        if (nome.isEmpty() || matricula.isEmpty() || cpf.isEmpty() || email.isEmpty() || perfil.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioAtualizado = Usuario(
            id = userId,
            nome = nome,
            matricula = matricula,
            cpf = cpf,
            email = email,
            perfil = perfil,
            usuario = "" // Mantemos o usuário original (não editável aqui)
        )

        usersCollection.document(userId)
            .set(usuarioAtualizado)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                voltarParaMain15()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao atualizar usuário: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun voltarParaMain15() {
        val intent = Intent(this, MainActivity15::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_item_home)?.setOnClickListener {
            val intent = Intent(this, MainActivity20::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.nav_item_users)?.setOnClickListener {
            voltarParaMain15()
        }

        findViewById<LinearLayout>(R.id.nav_item_settings)?.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }
    }
}