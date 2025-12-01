package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
// Imports necessários para o Firebase (limpos e corretos)
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class MainActivity4 : BaseActivity() {

    // Instância do banco de dados do Firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Adapters para as listas
    private lateinit var myBooksAdapter: BookAdapter
    private lateinit var suggestionsAdapter: BookAdapter

    // Listas que vão guardar os livros buscados do Firebase
    private val myBooksList = mutableListOf<Book>()
    private val suggestionsList = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        // 1. Prepara as RecyclerViews com os adapters (as listas ainda estão vazias)
        setupRecyclerViews()

        // 2. MANDA BUSCAR os livros do Firebase assim que a tela é criada
        fetchBooksFromFirestore()

        // 3. Configura o resto da interface (botões e navegação)
        setupUIButtons()
        setupBottomNavigation()
    }

    /**
     * Prepara as RecyclerViews e conecta os adapters às listas (que ainda estão vazias).
     */
    private fun setupRecyclerViews() {
        // Configura a RecyclerView "Meus Livros"
        val meusLivrosRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_meus_livros)
        meusLivrosRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // --- CORREÇÃO AQUI ---
        // Passamos uma ação de clique vazia, pois não queremos que nada aconteça ao clicar aqui.
        myBooksAdapter = BookAdapter(myBooksList) { /* Sem ação de clique */ }
        meusLivrosRecyclerView.adapter = myBooksAdapter

        // Configura a RecyclerView "Sugestões"
        val sugestoesRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_sugestoes)
        sugestoesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // --- CORREÇÃO AQUI ---
        suggestionsAdapter = BookAdapter(suggestionsList) { /* Sem ação de clique */ }
        sugestoesRecyclerView.adapter = suggestionsAdapter
    }

    /**
     * Busca os documentos da coleção "livros" no Firestore.
     */
    private fun fetchBooksFromFirestore() {
        db.collection("livros") // Pega a sua coleção "livros"
            .get() // Pega todos os documentos dentro dela
            .addOnSuccessListener { documents ->
                // SE DER CERTO:
                if (documents != null) {
                    Log.d("Firestore", "Sucesso! ${documents.size()} livros encontrados.")

                    // Limpa as listas para não duplicar os livros se a função for chamada de novo
                    myBooksList.clear()
                    suggestionsList.clear()

                    // O Firestore converte os documentos em objetos da nossa classe Book
                    val books = documents.toObjects<Book>()

                    // Adiciona os livros buscados às nossas listas
                    myBooksList.addAll(books)
                    suggestionsList.addAll(books)

                    // AVISA OS ADAPTERS que os dados mudaram. Isso faz a tela se redesenhar com os livros!
                    myBooksAdapter.notifyDataSetChanged()
                    suggestionsAdapter.notifyDataSetChanged()
                } else {
                    Log.d("Firestore", "Sucesso! Nenhum documento encontrado.")
                }
            }
            .addOnFailureListener { exception ->
                // SE DER ERRO:
                Log.w("Firestore", "Erro ao buscar documentos: ", exception)
            }
    }

    /**
     * Configura os cliques dos botões da interface (Configurações, Chatbot e SearchView).
     */
    private fun setupUIButtons() {
        val settingsButton = findViewById<ImageButton>(R.id.button_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }

        val chatbotButton = findViewById<ImageButton>(R.id.iconechatbothome)
        chatbotButton.setOnClickListener {
            val intent = Intent(this, MainActivity14::class.java)
            startActivity(intent)
        }

        // --- INÍCIO DA ALTERAÇÃO ---
        // Adiciona a funcionalidade de clique para o novo botão de eventos
        val buttonEventos = findViewById<ImageButton>(R.id.button_eventos)
        buttonEventos.setOnClickListener {
            // Cria a "intenção" de abrir a sua nova tela de eventos (MainActivity33)
            val intent = Intent(this, MainActivity33::class.java)
            // Executa a navegação, abrindo a tela
            startActivity(intent)
        }
        // --- FIM DA ALTERAÇÃO ---

        // --- CÓDIGO NOVO ADICIONADO AQUI ---
        // Encontra a barra de pesquisa pelo ID que está no seu activity_main4.xml
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.search_view)

        // Configura um "ouvinte de clique". Quando o usuário tocar na barra,
        // o código dentro das chaves será executado.
        searchView.setOnClickListener {
            // Cria a intenção de abrir a tela de busca (SearchActivity)
            val intent = Intent(this, SearchActivity::class.java)
            // Inicia a nova tela
            startActivity(intent)
        }

        // Impede que o teclado abra automaticamente na tela principal
        searchView.isFocusable = false
        searchView.clearFocus()
        // --- FIM DO CÓDIGO NOVO ---
    }

    /**
     * Configura a lógica de cliques para a BottomNavigationView.
     */
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            var intent: Intent? = null
            when (item.itemId) {
                R.id.nav_profile -> intent = Intent(this, MainActivity8::class.java)
                R.id.nav_library -> intent = Intent(this, MainActivity31::class.java)
                R.id.nav_home -> { /* Não faz nada */ }
                R.id.nav_donate -> intent = Intent(this, MainActivity32::class.java)
                R.id.nav_notifications -> intent = Intent(this, MainActivity7::class.java)
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            true
        }

        bottomNavigationView.setOnItemReselectedListener {
            // Não faz nada intencionalmente
        }
    }
}
