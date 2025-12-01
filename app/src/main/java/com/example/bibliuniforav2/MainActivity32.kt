package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class MainActivity32 :  BaseActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var emprestimosAdapter: EmprestimoAdapter
    private val emprestimosList = mutableListOf<Book>()

    // 🔑 UID do usuário logado
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main32)

        // 🔑 Obtém o UID do usuário logado
        currentUserId = auth.currentUser?.uid ?: ""

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Usuário não logado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchEmprestimosFromFirestore()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_emprestimos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        emprestimosAdapter = EmprestimoAdapter(emprestimosList) { book ->
            showRenewConfirmationDialog(book)
        }
        recyclerView.adapter = emprestimosAdapter
    }

    private fun fetchEmprestimosFromFirestore() {
        // 🔑 FILTRA APENAS EMPRÉSTIMOS DO USUÁRIO LOGADO
        db.collection("emprestimos")
            .whereEqualTo("uidAuth", currentUserId) // ← FILTRO IMPORTANTE!
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val books = documents.map { document ->
                        val book = document.toObject(Book::class.java)
                        book.id = document.id
                        book
                    }
                    emprestimosList.clear()
                    emprestimosList.addAll(books)
                    emprestimosAdapter.notifyDataSetChanged()

                    // Mostra quantos empréstimos foram encontrados
                    Toast.makeText(this, "Encontrados ${books.size} empréstimos", Toast.LENGTH_SHORT).show()
                } else {
                    emprestimosList.clear()
                    emprestimosAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Você não tem empréstimos ativos", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.w("Firestore", "Erro ao buscar empréstimos: ", exception)
                Toast.makeText(this, "Erro ao carregar seus empréstimos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRenewConfirmationDialog(book: Book) {
        AlertDialog.Builder(this)
            .setTitle("Renovar Empréstimo")
            .setMessage("Deseja renovar o empréstimo de \"${book.nome}\" por mais 7 dias?")
            .setPositiveButton("Sim") { _, _ -> renewLoan(book) }
            .setNegativeButton("Não", null)
            .setIcon(R.drawable.logo_unifor)
            .show()
    }

    private fun renewLoan(book: Book) {
        if (book.id.isBlank()) {
            Toast.makeText(this, "Erro: Não foi possível identificar o empréstimo.", Toast.LENGTH_SHORT).show()
            return
        }

        val novaDataDevolucao = book.dataDevolucao + (7 * 24 * 60 * 60 * 1000L)

        db.collection("emprestimos").document(book.id)
            .update("dataDevolucao", novaDataDevolucao)
            .addOnSuccessListener {
                Toast.makeText(this, "Livro renovado com sucesso!", Toast.LENGTH_SHORT).show()
                gerarNotificacao("Empréstimo Renovado", "Seu empréstimo de \"${book.nome}\" foi renovado.", "RENOVACAO")
                fetchEmprestimosFromFirestore()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Falha ao renovar empréstimo.", e)
                Toast.makeText(this, "Falha ao renovar. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gerarNotificacao(titulo: String, mensagem: String, tipo: String) {
        val novaNotificacao = Notification(
            titulo = titulo,
            mensagem = mensagem,
            tipo = tipo,
            uidAuth = currentUserId // ← AGORA SALVA O UID DO USUÁRIO
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

    private fun setupBottomNavigation() {
        val bottomNavView: BottomNavigationView? = findViewById(R.id.bottom_navigation_placeholder)
            ?: findViewById(R.id.bottom_navigation)

        bottomNavView?.selectedItemId = R.id.nav_donate

        bottomNavView?.setOnItemSelectedListener { item ->
            var intent: Intent? = null
            when (item.itemId) {
                R.id.nav_profile -> intent = Intent(this, MainActivity8::class.java)
                R.id.nav_library -> intent = Intent(this, MainActivity31::class.java)
                R.id.nav_home -> intent = Intent(this, MainActivity4::class.java)
                R.id.nav_donate -> { /* Já estamos aqui */ }
                R.id.nav_notifications -> intent = Intent(this, MainActivity7::class.java)
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            true
        }
        bottomNavView?.setOnItemReselectedListener {}
    }
}