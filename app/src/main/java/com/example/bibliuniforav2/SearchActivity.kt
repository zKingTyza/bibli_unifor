package com.example.bibliuniforav2

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class SearchActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var searchAdapter: SearchAdapter
    private val allBooksList = mutableListOf<Book>()
    private val searchResultsList = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupToolbar()
        setupRecyclerView()
        receiveBookList()
        setupSearchView()
    }

    private fun receiveBookList() {
        val bookListFromIntent: ArrayList<Book>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("ALL_BOOKS_LIST", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("ALL_BOOKS_LIST")
        }

        if (bookListFromIntent != null && bookListFromIntent.isNotEmpty()) {
            allBooksList.clear()
            allBooksList.addAll(bookListFromIntent)
            filterBooks(null) // Usa a lista recebida
        } else {
            fetchAllBooks() // Se não recebeu, busca no Firebase
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_search_results)
        val enableRental = intent.getBooleanExtra("ENABLE_RENTAL", false)

        searchAdapter = if (enableRental) {
            SearchAdapter(searchResultsList) { book ->
                showRentalConfirmationDialog(book)
            }
        } else {
            SearchAdapter(searchResultsList) { /* Sem ação de clique */ }
        }
        recyclerView.adapter = searchAdapter
    }

    private fun showRentalConfirmationDialog(book: Book) {
        if (book.quantidade <= 0) {
            Toast.makeText(this, "Este livro não está mais disponível.", Toast.LENGTH_SHORT).show()
            return
        }

        checkIfAlreadyRented(book) { isRented ->
            if (isRented) {
                Toast.makeText(this, "Você já possui um empréstimo deste livro.", Toast.LENGTH_LONG).show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar Aluguel")
                    .setMessage("Você deseja alugar o livro \"${book.nome}\"?")
                    .setPositiveButton("Sim") { _, _ -> rentBook(book) }
                    .setNegativeButton("Não", null)
                    .setIcon(R.drawable.logo_unifor)
                    .show()
            }
        }
    }

    private fun checkIfAlreadyRented(book: Book, onComplete: (Boolean) -> Unit) {
        // Futuramente, adicionar: .whereEqualTo("userId", firebaseAuth.currentUser.uid)
        db.collection("emprestimos")
            .whereEqualTo("nome", book.nome)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(!documents.isEmpty)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar empréstimos existentes.", Toast.LENGTH_SHORT).show()
                onComplete(true) // Bloqueia por segurança
            }
    }

    private fun rentBook(book: Book) {
        if (book.id.isBlank()) {
            Toast.makeText(this, "Erro crítico: ID do livro não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val bookRef = db.collection("livros").document(book.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(bookRef)
            val currentQuantity = snapshot.getLong("quantidade")?.toInt() ?: 0

            if (currentQuantity > 0) {
                transaction.update(bookRef, "quantidade", currentQuantity - 1)
                val emprestimoRef = db.collection("emprestimos").document()
                val novoEmprestimo = hashMapOf(
                    "id" to emprestimoRef.id,
                    "nome" to book.nome,
                    "autor" to book.autor,
                    "capa" to book.capa,
                    "dataEmprestimo" to System.currentTimeMillis(),
                    "dataDevolucao" to System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                )
                transaction.set(emprestimoRef, novoEmprestimo)
                null
            } else {
                throw FirebaseFirestoreException("Livro indisponível no momento.", FirebaseFirestoreException.Code.ABORTED)
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "${book.nome} alugado com sucesso!", Toast.LENGTH_SHORT).show()

            // --- ALTERAÇÃO APLICADA AQUI ---
            // Gera uma notificação no Firebase após o aluguel ser bem-sucedido.
            gerarNotificacao("Novo Empréstimo!", "O livro \"${book.nome}\" foi alugado.", "ALUGUEL")

            finish() // Fecha a tela de busca e volta para a anterior
        }.addOnFailureListener { e ->
            Log.w("FirestoreTransaction", "Falha ao alugar em SearchActivity: ", e)
            Toast.makeText(this, "Falha ao alugar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Buscar Livro"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchAllBooks() {
        db.collection("livros")
            .get()
            .addOnSuccessListener { documents ->
                val books = documents.map { document ->
                    val book = document.toObject(Book::class.java)
                    book.id = document.id
                    book
                }
                allBooksList.clear()
                allBooksList.addAll(books)
                filterBooks(null)
            }.addOnFailureListener { exception ->
                Log.w("SearchActivity", "Erro ao carregar livros.", exception)
                Toast.makeText(this, "Não foi possível carregar os livros.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.search_view_activity)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBooks(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterBooks(newText)
                return true
            }
        })
    }

    // Função para gerar uma notificação no Firebase
    private fun gerarNotificacao(titulo: String, mensagem: String, tipo: String) {
        val db = FirebaseFirestore.getInstance()
        // Usa a sua classe Notification, mas preenche apenas os campos que o Firebase precisa
        val novaNotificacao = Notification(titulo = titulo, mensagem = mensagem, tipo = tipo)

        db.collection("notificacoes")
            .add(novaNotificacao)
            .addOnSuccessListener {
                Log.d("Notificacao", "Notificação de $tipo salva com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Notificacao", "Erro ao salvar notificação", e)
            }
    }

    private fun filterBooks(query: String?) {
        searchResultsList.clear()
        if (query.isNullOrBlank()) {
            searchResultsList.addAll(allBooksList)
        } else {
            val lowerCaseQuery = query.lowercase().trim()
            val filtered = allBooksList.filter { book ->
                book.nome.lowercase().contains(lowerCaseQuery) || book.autor.lowercase().contains(lowerCaseQuery)
            }
            searchResultsList.addAll(filtered)
        }
        searchAdapter.notifyDataSetChanged()
    }
}
