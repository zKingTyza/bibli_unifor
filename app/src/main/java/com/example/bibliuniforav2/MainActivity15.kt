package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity15 : AppCompatActivity() {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var userAdapter: UserAdapter

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("usuarios")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main15)

        val rootView = findViewById<android.view.View>(R.id.main)
        rootView?.let { v ->
            ViewCompat.setOnApplyWindowInsetsListener(v) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        initViews()
        setupRecyclerView()
        setupSearch()
        loadUsersFromFirebase()
        setupBottomNavigation()
    }

    private fun initViews() {
        usersRecyclerView = findViewById(R.id.usersRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            users = emptyList(),
            onEditClick = { usuario ->
                // Navegar para tela de edição (Main16)
                val intent = Intent(this, MainActivity16::class.java)
                intent.putExtra("USER_ID", usuario.id)
                startActivity(intent)
            },
            onDeleteClick = { usuario ->
                // Mostrar confirmação de exclusão
                showDeleteConfirmation(usuario)
            }
        )

        usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity15)
            adapter = userAdapter
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchButton.setOnClickListener {
            userAdapter.filter(searchEditText.text.toString())
        }
    }

    private fun loadUsersFromFirebase() {
        usersCollection.get()
            .addOnSuccessListener { result ->
                val usuarios = mutableListOf<Usuario>()
                for (document in result) {
                    val usuario = document.toObject(Usuario::class.java).copy(id = document.id)
                    usuarios.add(usuario)
                }
                userAdapter.updateUsers(usuarios)
                Toast.makeText(this, "Usuários carregados com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar usuários: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDeleteConfirmation(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Usuário")
            .setMessage("Tem certeza que deseja excluir ${usuario.nome}?")
            .setPositiveButton("Excluir") { dialog, _ ->
                deleteUser(usuario)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteUser(usuario: Usuario) {
        if (usuario.id.isNotEmpty()) {
            usersCollection.document(usuario.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    loadUsersFromFirebase() // Recarrega a lista
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao excluir usuário: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_item_home)?.setOnClickListener {
            irParaMainActivity20()
        }

        findViewById<LinearLayout>(R.id.nav_item_settings)?.setOnClickListener {
            irParaMainActivity5()
        }
    }

    private fun irParaMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun irParaMainActivity5() {
        val intent = Intent(this, MainActivity5::class.java)
        startActivity(intent)
    }
}