package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bibliuniforav2.databinding.ActivityMain20Binding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity20 : AppCompatActivity() {

    private lateinit var binding: ActivityMain20Binding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val eventosRepository by lazy { EventosRepository(db) }

    // ✅ Agora usa o BookiAdapter e a classe booki
    private lateinit var livroAdapter: BookiAdapter // <-- CORRIGIDO
    private lateinit var eventoAdapter: EventoAdapter
    private var allBooksList = mutableListOf<booki>() // <-- CORRIGIDO
    private var allEventsList = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain20Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LAUNCH", "MainActivity20 iniciada.")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.navBottomBar.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupRecyclerViews()
        setupClickListeners()

        fetchBooksFromFirestore()
        fetchEventsFromRepository()
    }

    // -----------------------------------------------------------------------------------
    // FUNÇÕES DE BUSCA
    // -----------------------------------------------------------------------------------

    private fun fetchBooksFromFirestore() {
        Log.d("FIREBASE_FETCH", "Iniciando busca por Livros...")
        db.collection("livros")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("FIREBASE_FETCH", "Busca de livros bem-sucedida. Documentos: ${documents?.size()}")

                allBooksList.clear()

                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        try {
                            // CORRIGIDO: Deve converter para booki, não Book
                            val book = document.toObject(booki::class.java)
                            book?.let {
                                allBooksList.add(it)
                            } ?: run {
                                Log.e("FIREBASE_ERROR", "Falha ao mapear documento para booki (null): ${document.id}")
                            }
                        } catch (e: Exception) {
                            Log.e("FIREBASE_ERROR", "Erro ao converter documento ${document.id} para booki: ${e.message}", e)
                        }
                    }

                    Log.d("FIREBASE_DATA", "Total de livros carregados (incluindo duplicatas): ${allBooksList.size}")

                    val uniqueBooks = allBooksList.distinctBy { Pair(it.nome, it.autor) }
                    Log.d("FIREBASE_DATA", "Livros únicos para display: ${uniqueBooks.size}")

                    if (uniqueBooks.isNotEmpty()) {
                        Log.d("FIREBASE_DATA", "Primeiro livro mapeado: ${uniqueBooks.first().nome}, ID: ${uniqueBooks.first().id}")
                    }

                    // CORRIGIDO: livroAdapter.setBooks espera List<booki>
                    livroAdapter.setBooks(uniqueBooks) // <-- CORRIGIDO AQUI

                } else {
                    Log.d("FIREBASE_FETCH", "Nenhum livro encontrado ou documentos nulos.")
                    // CORRIGIDO: livroAdapter.setBooks espera List<booki>
                    livroAdapter.setBooks(emptyList()) // <-- CORRIGIDO AQUI
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FIREBASE_ERROR", "Erro ao buscar livros: ", exception)
                Toast.makeText(this, "Erro ao carregar livros: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchEventsFromRepository() {
        Log.d("REPOSITORY_FETCH", "Iniciando busca por Eventos via Repositório...")

        lifecycleScope.launch {
            try {
                val events = eventosRepository.getAllEventos()

                if (events.isNotEmpty()) {
                    Log.d("REPOSITORY_DATA", "Sucesso! ${events.size} eventos recebidos do Repositório.")
                    allEventsList.clear()
                    allEventsList.addAll(events)
                    val primeiroEvento = allEventsList.firstOrNull()
                    Log.d("REPOSITORY_DATA", "Primeiro Evento (Título): ${primeiroEvento?.titulo}, Data: ${primeiroEvento?.data}, Hora: ${primeiroEvento?.hora}")

                    eventoAdapter.submitList(allEventsList.toList())
                    Log.d("FIREBASE_ADAPTER", "Lista de Eventos submetida ao adapter.")

                } else {
                    Log.w("REPOSITORY_DATA", "Nenhum evento retornado do Repositório. Verifique regras de segurança/mapeamento ou se há eventos no BD.")
                    Toast.makeText(this@MainActivity20, "Nenhum evento ativo encontrado.", Toast.LENGTH_SHORT).show()
                    eventoAdapter.submitList(emptyList())
                }
            } catch (e: Exception) {
                Log.e("REPOSITORY_FETCH_ERROR", "Erro inesperado ao buscar eventos do repositório: ${e.message}", e)
                Toast.makeText(this@MainActivity20, "Erro ao carregar eventos: ${e.message}", Toast.LENGTH_LONG).show()
                eventoAdapter.submitList(emptyList())
            }
        }
    }


    // -----------------------------------------------------------------------------------
    // CONFIGURAÇÃO E NAVEGAÇÃO
    // -----------------------------------------------------------------------------------

    private fun setupRecyclerViews() {
        Log.d("SETUP_UI", "Configurando RecyclerViews.")

        binding.recyclerViewLivros.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // ✅ INSTANCIAÇÃO DO BOOKIADAPTER PARA MAINACTIVITY20 (sem botões de edit/delete)
        livroAdapter = BookiAdapter( // <-- CORRIGIDO
            emptyList(),
            onBookClickListener = { livroClicado ->
                handleBookClick(livroClicado)
            },
            onEditClickListener = null, // Não usado na MainActivity20
            onDeleteClickListener = null, // Não usado na MainActivity20
            useSearchLayout = false // Usa o item_livro.xml
        )
        binding.recyclerViewLivros.adapter = livroAdapter

        binding.recyclerViewEventos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        eventoAdapter = EventoAdapter { eventoClicado ->
            irParaMainActivity10(eventoClicado)
        }
        binding.recyclerViewEventos.adapter = eventoAdapter
    }

    // CORRIGIDO: Parâmetro deve ser do tipo booki
    private fun handleBookClick(livroUnicoClicado: booki) {
        val exemplaresDoLivro = allBooksList.filter {
            it.nome == livroUnicoClicado.nome && it.autor == livroUnicoClicado.autor
        }
        val intent = Intent(this, MainActivity21::class.java)
        intent.putExtra("LIVRO_PRINCIPAL_ID", livroUnicoClicado.id)
        // CORRIGIDO: putParcelableArrayListExtra espera ArrayList<booki>
        intent.putParcelableArrayListExtra("LISTA_EXEMPLARES", ArrayList(exemplaresDoLivro))
        // CORRIGIDO: putParcelableArrayListExtra espera ArrayList<booki>
        intent.putParcelableArrayListExtra("LISTA_COMPLETA_LIVROS", ArrayList(allBooksList))
        startActivity(intent)
    }

    private fun setupClickListeners() {
        binding.buttonSettings.setOnClickListener {
            irParaMainActivity5()
        }
        binding.imageButtonHome.setOnClickListener {
            Toast.makeText(this, "Você já está na Home", Toast.LENGTH_SHORT).show()
        }
        binding.textInputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                irParaMainActivity22()
                true
            } else { false }
        }
        binding.imageButtonBooks.setOnClickListener { irParaMainActivity22() }
        binding.imageButtonNotifications.setOnClickListener { irParaMainActivity25() }
        binding.imageButtonEvents.setOnClickListener {
            irParaMainActivity13()
        }
        binding.imageButtonProfile.setOnClickListener {
            irParaMainActivity15()
        }
    }

    private fun irParaMainActivity5() {
        val intent = Intent(this, MainActivity5::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        val textoBusca = binding.textInputEditTextSearch.text.toString()
        intent.putExtra("TEXTO_BUSCA", textoBusca)
        // CORRIGIDO: putParcelableArrayListExtra espera ArrayList<booki>
        intent.putParcelableArrayListExtra("LISTA_COMPLETA_LIVROS", ArrayList(allBooksList))
        startActivity(intent)
    }

    private fun irParaMainActivity25() {
        val intent = Intent(this, MainActivity25::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity10(evento: Evento) {
        val intent = Intent(this, MainActivity10::class.java)
        intent.putExtra("EVENTO_ID", evento.id)
        startActivity(intent)
    }

    private fun irParaMainActivity13() {
        val intent = Intent(this, MainActivity13::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity15() {
        val intent = Intent(this, MainActivity15::class.java)
        startActivity(intent)
    }
}
