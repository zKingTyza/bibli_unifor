package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObjects

class MainActivity31 :  BaseActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var suggestionsAdapter: BookAdapter
    private lateinit var newReleasesAdapter: BookAdapter
    private val suggestionsList = mutableListOf<Book>()
    private val newReleasesList = mutableListOf<Book>()
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main31)

        setupRecyclerViews()
        fetchBooksFromFirestore()
        setupBottomNavigation()
        setupSearch()
    }

    private fun setupSearch() {
        val searchView = findViewById<SearchView>(R.id.search_view_alugueis)

        searchView.setOnClickListener {
            if (suggestionsList.isNotEmpty()) {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putParcelableArrayListExtra("ALL_BOOKS_LIST", ArrayList(suggestionsList))
                intent.putExtra("ENABLE_RENTAL", true)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Carregando livros, tente novamente em um instante.", Toast.LENGTH_SHORT).show()
            }
        }
        searchView.isFocusable = false
        searchView.clearFocus()
    }

    private fun setupRecyclerViews() {
        val suggestionsRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_sugestoes_aluguel)
        suggestionsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        suggestionsAdapter = BookAdapter(suggestionsList) { book ->
            showRentalConfirmationDialog(book)
        }
        suggestionsRecyclerView.adapter = suggestionsAdapter

        val newReleasesRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_lancamentos)
        newReleasesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newReleasesAdapter = BookAdapter(newReleasesList) { book ->
            showRentalConfirmationDialog(book)
        }
        newReleasesRecyclerView.adapter = newReleasesAdapter
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
        val currentUserId = auth.currentUser?.uid ?: ""

        if (currentUserId.isEmpty()) {
            onComplete(false) // Se não tem usuário, não pode ter empréstimo
            return
        }

        // 🔑 AGORA VERIFICA POR LIVRO + USUÁRIO ESPECÍFICO
        db.collection("emprestimos")
            .whereEqualTo("nome", book.nome)
            .whereEqualTo("uidAuth", currentUserId) // ← FILTRO IMPORTANTE!
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(!documents.isEmpty)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar empréstimos existentes.", Toast.LENGTH_SHORT).show()
                onComplete(true) // Em caso de erro, assume que já tem (para segurança)
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
                    "dataDevolucao" to System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L),
                    "uidAuth" to auth.currentUser?.uid
                )
                transaction.set(emprestimoRef, novoEmprestimo)
                null
            } else {
                throw FirebaseFirestoreException(
                    "Livro indisponível no momento.",
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "${book.nome} alugado com sucesso!", Toast.LENGTH_SHORT).show()

            // --- ALTERAÇÃO APLICADA AQUI ---
            // Gera uma notificação no Firebase após o aluguel ser bem-sucedido.
            gerarNotificacao("Novo Empréstimo!", "O livro \"${book.nome}\" foi alugado.", "ALUGUEL")

            fetchBooksFromFirestore()
        }.addOnFailureListener { e ->
            Log.w("FirestoreTransaction", "Falha ao alugar em MainActivity31: ", e)
            Toast.makeText(this, "Falha ao alugar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Função para gerar uma notificação no Firebase - CORRIGIDA
    private fun gerarNotificacao(titulo: String, mensagem: String, tipo: String) {
        val currentUserId = auth.currentUser?.uid ?: ""

        val novaNotificacao = Notification(
            titulo = titulo,
            mensagem = mensagem,
            tipo = tipo,
            uidAuth = currentUserId // ← AGORA SALVA O UID DO USUÁRIO!
        )

        db.collection("notificacoes")
            .add(novaNotificacao)
            .addOnSuccessListener {
                Log.d("Notificacao", "Notificação de $tipo salva com sucesso para o usuário $currentUserId!")
            }
            .addOnFailureListener { e ->
                Log.e("Notificacao", "Erro ao salvar notificação", e)
            }
    }

    private fun fetchBooksFromFirestore() {
        db.collection("livros")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val books = documents.map { document ->
                        val book = document.toObject(Book::class.java)
                        book.id = document.id
                        book
                    }

                    suggestionsList.clear()
                    newReleasesList.clear()
                    suggestionsList.addAll(books)
                    newReleasesList.addAll(books.shuffled())
                    suggestionsAdapter.notifyDataSetChanged()
                    newReleasesAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Erro ao buscar livros: ", exception)
            }
    }

    private fun setupBottomNavigation() {
        val bottomNavView: BottomNavigationView? = findViewById(R.id.bottom_navigation)
            ?: findViewById(R.id.bottom_navigation_placeholder)

        bottomNavView?.let { navView ->
            navView.selectedItemId = R.id.nav_library
            navView.setOnItemSelectedListener { item ->
                var intent: Intent? = null
                when (item.itemId) {
                    R.id.nav_profile -> intent = Intent(this, MainActivity8::class.java)
                    R.id.nav_library -> { /* Não faz nada */ }
                    R.id.nav_home -> intent = Intent(this, MainActivity4::class.java)
                    R.id.nav_donate -> intent = Intent(this, MainActivity32::class.java)
                    R.id.nav_notifications -> intent = Intent(this, MainActivity7::class.java)
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                }
                true
            }
            navView.setOnItemReselectedListener {
                // Não faz nada
            }
        }
    }
}
