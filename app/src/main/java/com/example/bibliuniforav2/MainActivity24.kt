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
import com.bumptech.glide.Glide
import com.example.bibliuniforav2.databinding.ActivityMain24Binding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity24 : AppCompatActivity() {

    private lateinit var binding: ActivityMain24Binding
    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("livros")
    private var livroOriginal: booki? = null // Usa booki

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain24Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.navBottomBar.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val livroParaEditar: booki? // Usa booki
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            livroParaEditar = intent.getParcelableExtra("LIVRO_PARA_EDITAR", booki::class.java)
        } else {
            @Suppress("DEPRECATION")
            livroParaEditar = intent.getParcelableExtra("LIVRO_PARA_EDITAR") as? booki
        }

        if (livroParaEditar != null) {
            livroOriginal = livroParaEditar
            popularDados(livroParaEditar)
        } else {
            Toast.makeText(this, "Erro: Não foi possível carregar o livro para edição.", Toast.LENGTH_LONG).show()
            finish()
        }

        setupClickListeners()
    }

    private fun popularDados(livro: booki) { // Usa booki
        // Carrega a imagem da URL usando Glide
        if (livro.capa.isNotEmpty()) {
            Glide.with(this)
                .load(livro.capa)
                .placeholder(R.drawable.default_book_cover) // Imagem de placeholder
                .error(R.drawable.image_error) // Imagem de erro
                .into(binding.imageViewBookCover)
        } else {
            binding.imageViewBookCover.setImageResource(R.drawable.default_book_cover)
        }

        binding.editTextTitulo.setText(livro.nome)
        binding.editTextEditora.setText(livro.editora)
        binding.editTextAutor.setText(livro.autor)
        binding.editTextUrlCapa.setText(livro.capa)
        binding.editTextQuantidade.setText(livro.quantidade.toString())
    }

    private fun setupClickListeners() {
        // Clicks da barra de navegação inferior
        binding.navBottomBar.findViewById<View>(R.id.imageButtonHome).setOnClickListener {
            redirectToMainActivity20()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonBooks).setOnClickListener {
            irParaMainActivity22() // Vai para a lista de gerenciamento de livros
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonProfile).setOnClickListener {
            irParaMainActivity15()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonEvents).setOnClickListener {
            irParaMainActivity13()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonNotifications).setOnClickListener {
            irParaMainActivity25()
        }

        binding.buttonSalvarAlteracoes.setOnClickListener {
            salvarAlteracoesDoLivro()
        }
    }

    private fun salvarAlteracoesDoLivro() {
        val livroId = livroOriginal?.id
        if (livroId == null || livroId.isEmpty()) {
            Toast.makeText(this, "Erro: ID do livro não encontrado para salvar.", Toast.LENGTH_LONG).show()
            return
        }

        val novoNome = binding.editTextTitulo.text.toString().trim()
        val novoAutor = binding.editTextAutor.text.toString().trim()
        val novaCapaUrl = binding.editTextUrlCapa.text.toString().trim()
        val novaEditora = binding.editTextEditora.text.toString().trim()
        val novaQuantidadeStr = binding.editTextQuantidade.text.toString().trim()

        if (novoNome.isEmpty() || novoAutor.isEmpty() || novaEditora.isEmpty() || novaQuantidadeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios (Título, Autor, Editora, Quantidade).", Toast.LENGTH_LONG).show()
            return
        }

        val novaQuantidade: Int
        try {
            novaQuantidade = novaQuantidadeStr.toInt()
            if (novaQuantidade < 0) {
                Toast.makeText(this, "A quantidade não pode ser negativa.", Toast.LENGTH_LONG).show()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Quantidade inválida. Por favor, insira um número inteiro.", Toast.LENGTH_LONG).show()
            return
        }

        // ✅ Cria um 'booki' atualizado
        val livroAtualizado = livroOriginal!!.copy(
            nome = novoNome,
            autor = novoAutor,
            capa = novaCapaUrl.ifEmpty { "https://firebasestorage.googleapis.com/v0/b/bibliuniforav2.appspot.com/o/BookImages%2Fdefault_book_cover.jpg?alt=media&token=e937397b-944e-4f3b-b6d3-92f725a3a2a4" },
            editora = novaEditora,
            quantidade = novaQuantidade
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                booksCollection.document(livroId).set(livroAtualizado).await()
                with(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity24, "Livro '${livroAtualizado.nome}' atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity21(livroAtualizado) // Redireciona para MainActivity21 com o livro editado
                }
            } catch (e: Exception) {
                Log.e("MainActivity24", "Erro ao salvar alterações do livro: ${e.message}", e)
                with(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity24, "Erro ao salvar alterações: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Função auxiliar para redirecionar para MainActivity20 e limpar a pilha.
     */
    private fun redirectToMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish() // Finaliza MainActivity24 para que o usuário não possa voltar para ela
    }

    private fun navigateToMainActivity21(book: booki) {
        val intent = Intent(this, MainActivity21::class.java)
        intent.putExtra("LIVRO_PRINCIPAL_ID", book.id) // Passa o ID para MainActivity21 carregar todos os exemplares
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
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