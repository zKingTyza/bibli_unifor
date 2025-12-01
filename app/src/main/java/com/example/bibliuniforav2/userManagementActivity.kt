package com.example.bibliuniforav2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserManagementActivity : AppCompatActivity() {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main15)

        initViews()
        setupRecyclerView()
        setupSearch()
        //setupBottomNavigation()
    }

    private fun initViews() {
        //usersRecyclerView = findViewById(R.id.usersRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
    }

    private fun setupRecyclerView() {
        // Dados de exemplo baseados na sua tabela
        val users = listOf(
            Usuario("admin", "ADMINISTRADOR", "533.638.157.86", "ADMINISTRADOR"),
            Usuario("rafaela bandeira", "RAFAELA BANDERA TEIXEIRA", "027.938.193.01", "GERENTE COMERCIAL"),
            Usuario("leitoia andrade", "LETICIA EMILY ANDRADE MELIGA", "041.486.613.47", "GESTOR COMERCIAL"),
            // Adicione os outros usuários aqui...
        )

        userAdapter = UserAdapter(
            users = users,
            onEditClick = { user ->
                // Abrir tela de edição do usuário
                openEditUserScreen(user)
            },
            onDeleteClick = { user ->
                // Mostrar confirmação de exclusão
                showDeleteConfirmation(user)
            }
        )

        usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserManagementActivity)
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

//    private fun setupBottomNavigation() {
//        val bottomBar = findViewById<LinearLayout>(R.id.bottomNavigationBar)
//
//        // Configurar os cliques dos itens da barra inferior
//        val homeItem = bottomBar.getChildAt(0) as LinearLayout
//        val usersItem = bottomBar.getChildAt(1) as LinearLayout
//        val settingsItem = bottomBar.getChildAt(2) as LinearLayout
//
//        homeItem.setOnClickListener {
//            // Navegar para Home
//        }
//
//        usersItem.setOnClickListener {
//            // Já está na tela de usuários
//        }
//
//        settingsItem.setOnClickListener {
//            // Navegar para Configurações
//        }
//    }

    private fun openEditUserScreen(user: Usuario) {
        // Implementar abertura da tela de edição
        // Intent(this, EditUserActivity::class.java).apply {
        //     putExtra("USER_DATA", user)
        //     startActivity(this)
        // }
    }

    private fun showDeleteConfirmation(user: Usuario) {
        // Implementar diálogo de confirmação de exclusão
        // AlertDialog.Builder(this)
        //     .setTitle("Excluir Usuário")
        //     .setMessage("Tem certeza que deseja excluir ${user.nome}?")
        //     .setPositiveButton("Excluir") { dialog, which ->
        //         // Excluir usuário
        //     }
        //     .setNegativeButton("Cancelar", null)
        //     .show()
    }
}

// Data class para representar um usuário
data class User(
    val usuario: String,
    val nome: String,
    val cpf: String,
    val perfil: String
)