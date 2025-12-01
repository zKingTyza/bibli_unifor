package com.example.bibliuniforav2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bibliuniforav2.databinding.ActivityMain23Binding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID // Para gerar IDs únicos

class MainActivity23 : AppCompatActivity() {

    private lateinit var binding: ActivityMain23Binding
    private val db = FirebaseFirestore.getInstance()

    private var livroBase: booki? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain23Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera o livro base (se estiver adicionando um exemplar a um livro existente)
        livroBase = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LIVRO_BASE", booki::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("LIVRO_BASE") as? booki
        }

        if (livroBase != null) {
            // Se um livro base foi passado, estamos adicionando um NOVO EXEMPLAR
            binding.textViewEditTitle.text = "Adicionar Novo Exemplar" // ✅ Ajustado para textViewEditTitle
            binding.textInputLayoutTitulo.visibility = View.GONE // ✅ Ajustado para textInputLayoutTitulo
            binding.textInputLayoutAutor.visibility = View.GONE // ✅ Ajustado para textInputLayoutAutor
            binding.textInputLayoutUrlCapa.visibility = View.GONE // ✅ Ajustado para textInputLayoutUrlCapa
            binding.editTextQuantidade.setText("1") // ✅ Ajustado para editTextQuantidade
            binding.editTextQuantidade.isEnabled = false // ✅ Ajustado para editTextQuantidade
            binding.buttonAdicionarExemplar.text = "Adicionar Exemplar" // ✅ Ajustado para buttonAdicionarExemplar

            Toast.makeText(this, "Adicionando exemplar para '${livroBase?.nome}'", Toast.LENGTH_SHORT).show()
        } else {
            // Se nenhum livro base foi passado, estamos adicionando um NOVO LIVRO
            binding.textViewEditTitle.text = "Adicionar Novo Livro" // ✅ Ajustado para textViewEditTitle
            binding.buttonAdicionarExemplar.text = "Salvar Novo Livro" // ✅ Ajustado para buttonAdicionarExemplar
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonAdicionarExemplar.setOnClickListener { // ✅ Ajustado para buttonAdicionarExemplar
            if (livroBase == null) {
                addNewBook()
            } else {
                addNewExemplar(livroBase!!)
            }
        }
        // ✅ Removido o listener para imageButtonBack, pois não existe no XML
        // Se você quiser um botão de voltar, adicione-o ao XML e crie o listener aqui.

        // Clicks da barra de navegação inferior
        binding.navBottomBar.findViewById<View>(R.id.imageButtonHome).setOnClickListener {
            redirectToMainActivity20()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonProfile).setOnClickListener {
            irParaMainActivity15()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonBooks).setOnClickListener {
            irParaMainActivity22() // Redireciona para a lista de livros
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonEvents).setOnClickListener {
            irParaMainActivity13()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonNotifications).setOnClickListener {
            irParaMainActivity25()
        }
    }

    private fun addNewBook() {
        val nome = binding.editTextTitulo.text.toString().trim() // ✅ Ajustado para editTextTitulo
        val autor = binding.editTextAutor.text.toString().trim() // ✅ Ajustado para editTextAutor
        val capa = binding.editTextUrlCapa.text.toString().trim() // ✅ Ajustado para editTextUrlCapa
        val quantidadeStr = binding.editTextQuantidade.text.toString().trim() // ✅ Ajustado para editTextQuantidade

        if (nome.isBlank() || autor.isBlank() || capa.isBlank() || quantidadeStr.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeStr.toIntOrNull()
        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(this, "Quantidade inválida. Deve ser um número maior que zero.", Toast.LENGTH_SHORT).show()
            return
        }

        val novoLivroId = UUID.randomUUID().toString()

        val novoLivro = booki(
            id = novoLivroId,
            nome = nome,
            autor = autor,
            capa = capa,
            quantidade = quantidade
        )

        db.collection("livros").document(novoLivroId)
            .set(novoLivro)
            .addOnSuccessListener {
                Toast.makeText(this, "Livro '${novoLivro.nome}' adicionado com sucesso!", Toast.LENGTH_LONG).show()
                Log.d("MainActivity23", "Novo livro adicionado com ID: $novoLivroId")
                navigateToMainActivity21(novoLivro)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao adicionar livro: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MainActivity23", "Erro ao adicionar novo livro:", e)
            }
    }

    private fun addNewExemplar(existingBook: booki) {
        val novoExemplarId = UUID.randomUUID().toString()

        val novoExemplar = booki(
            id = novoExemplarId,
            nome = existingBook.nome,
            autor = existingBook.autor,
            capa = existingBook.capa,
            quantidade = 1 // Sempre 1 para um exemplar individual
        )

        db.collection("livros").document(novoExemplarId)
            .set(novoExemplar)
            .addOnSuccessListener {
                Toast.makeText(this, "Novo exemplar para '${existingBook.nome}' adicionado!", Toast.LENGTH_LONG).show()
                Log.d("MainActivity23", "Novo exemplar adicionado com ID: $novoExemplarId para livro: ${existingBook.id}")
                navigateToMainActivity21(existingBook)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao adicionar exemplar: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MainActivity23", "Erro ao adicionar novo exemplar:", e)
            }
    }

    private fun navigateToMainActivity21(book: booki) {
        val intent = Intent(this, MainActivity21::class.java)
        intent.putExtra("LIVRO_PRINCIPAL_ID", book.id) // Passa o ID do livro principal para MainActivity21
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun redirectToMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        // Não passamos texto de busca ou lista completa aqui, pois MainActivity22 já carrega seus próprios dados
        startActivity(intent)
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
}