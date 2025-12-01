package com.example.bibliuniforav2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bibliuniforav2.databinding.ActivityMain22Binding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.ArrayList
import java.util.Locale

class MainActivity22 : AppCompatActivity() {

    private lateinit var binding: ActivityMain22Binding
    private lateinit var bookAdapter: BookiAdapter
    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("livros")

    private var allUniqueBooksFromDb: MutableList<booki> = mutableListOf()
    private var allBooksWithExemplars: MutableList<booki> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain22Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearchInput()
        setupClickListeners()

        fetchAllBooksFromFirestore()

        val initialSearchQuery = intent.getStringExtra("TEXTO_BUSCA")
        initialSearchQuery?.let {
            binding.searchEditText.setText(it)
            performSearch(it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookiAdapter(
            emptyList(),
            onBookClickListener = { bookClicked ->
                handleBookClick(bookClicked)
            },
            onEditClickListener = { bookToEdit ->
                handleEditClick(bookToEdit)
            },
            onDeleteClickListener = { bookToDelete ->
                deleteBook(bookToDelete)
            },
            useSearchLayout = true
        )
        binding.recyclerViewBooks.adapter = bookAdapter
    }

    private fun setupSearchInput() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                performSearch(query)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAllBooksFromFirestore() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val snapshot = booksCollection.get().await()
                allBooksWithExemplars.clear()
                allUniqueBooksFromDb.clear()

                if (!snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        try {
                            val book = document.toObject(booki::class.java)
                            book?.let {
                                allBooksWithExemplars.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity22", "Erro ao mapear documento para booki: ${document.id} - ${e.message}", e)
                        }
                    }
                    allUniqueBooksFromDb.addAll(allBooksWithExemplars.distinctBy { Pair(it.nome, it.autor) })
                    val currentQuery = binding.searchEditText.text.toString().trim()
                    performSearch(currentQuery)

                } else {
                    Log.d("MainActivity22", "Nenhum livro encontrado no Firestore.")
                    bookAdapter.setBooks(emptyList())
                    updateStatusMessage(true)
                }
            } catch (e: Exception) {
                Log.e("MainActivity22", "Erro ao buscar todos os livros do Firestore: ${e.message}", e)
                Toast.makeText(this@MainActivity22, "Erro ao carregar livros: ${e.message}", Toast.LENGTH_LONG).show()
                bookAdapter.setBooks(emptyList())
                updateStatusMessage(true)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            try {
                val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
                val filteredBooks = if (query.isBlank()) {
                    allUniqueBooksFromDb
                } else {
                    allUniqueBooksFromDb.filter { book ->
                        book.nome.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                                book.autor.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)
                    }
                }
                bookAdapter.setBooks(filteredBooks)
                updateStatusMessage(filteredBooks.isEmpty())

            } catch (e: Exception) {
                Log.e("MainActivity22", "Erro durante a pesquisa local de livros: ${e.message}", e)
                Toast.makeText(this@MainActivity22, "Erro na pesquisa: ${e.message}", Toast.LENGTH_LONG).show()
                bookAdapter.setBooks(emptyList())
                updateStatusMessage(true)
            }
        }
    }

    private fun updateStatusMessage(isEmpty: Boolean) {
        if (isEmpty) {
            binding.statusMessageTextView.text = "Nenhum livro encontrado."
            binding.statusMessageTextView.visibility = View.VISIBLE
        } else {
            binding.statusMessageTextView.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewBooks.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.statusMessageTextView.visibility = View.GONE
    }

    private fun handleBookClick(livroClicado: booki) {
        val intent = Intent(this, MainActivity21::class.java)
        intent.putExtra("LIVRO_PRINCIPAL_ID", livroClicado.id)

        val exemplaresDoLivro = allBooksWithExemplars.filter {
            it.nome == livroClicado.nome && it.autor == livroClicado.autor
        }
        // ✅ REMOVIDO <booki>
        intent.putParcelableArrayListExtra("LISTA_EXEMPLARES", ArrayList(exemplaresDoLivro))
        // ✅ REMOVIDO <booki>
        intent.putParcelableArrayListExtra("LISTA_COMPLETA_LIVROS", ArrayList(allBooksWithExemplars))
        startActivity(intent)
    }

    private fun handleEditClick(bookToEdit: booki) {
        Toast.makeText(this, "Editar livro: ${bookToEdit.nome}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity24::class.java)
        intent.putExtra("LIVRO_PARA_EDITAR", bookToEdit)
        startActivity(intent)
    }

    private fun deleteBook(bookToDelete: booki) {
        if (bookToDelete.id.isBlank()) {
            Toast.makeText(this, "Não foi possível excluir: ID do livro ausente.", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        lifecycleScope.launch {
            try {
                booksCollection.document(bookToDelete.id).delete().await()
                Toast.makeText(this@MainActivity22, "Livro '${bookToDelete.nome}' excluído com sucesso!", Toast.LENGTH_SHORT).show()
                redirectToMainActivity20()
            } catch (e: Exception) {
                Log.e("MainActivity22", "Erro ao excluir livro '${bookToDelete.id}': ${e.message}", e)
                Toast.makeText(this@MainActivity22, "Erro ao excluir livro: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun redirectToMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun setupClickListeners() {
        binding.imageButtonAdd.setOnClickListener {
            val intent = Intent(this, MainActivity23::class.java)
            startActivity(intent)
        }

        binding.navBottomBar.findViewById<ImageButton>(R.id.imageButtonHome).setOnClickListener {
            redirectToMainActivity20()
        }
        binding.navBottomBar.findViewById<ImageButton>(R.id.imageButtonProfile).setOnClickListener {
            irParaMainActivity15()
        }
        binding.navBottomBar.findViewById<ImageButton>(R.id.imageButtonBooks).setOnClickListener {
            Toast.makeText(this@MainActivity22, "Você já está na Gerenciamento de Livros", Toast.LENGTH_SHORT).show()
        }
        binding.navBottomBar.findViewById<ImageButton>(R.id.imageButtonEvents).setOnClickListener {
            irParaMainActivity13()
        }
        binding.navBottomBar.findViewById<ImageButton>(R.id.imageButtonNotifications).setOnClickListener {
            irParaMainActivity25()
        }
    }

    private fun irParaMainActivity25() {
        val intent = Intent(this, MainActivity25::class.java)
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

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}