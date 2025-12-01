package com.example.bibliuniforav2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bibliuniforav2.databinding.ActivityMain21Binding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class MainActivity21 : AppCompatActivity() {

    private lateinit var binding: ActivityMain21Binding
    private lateinit var exemplarAdapter: ExemplariAdapter
    private val db = FirebaseFirestore.getInstance()

    private var listaExemplaresMutavel = mutableListOf<booki>()
    private var listaCompletaLivros = mutableListOf<booki>()
    private var livroPrincipal: booki? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain21Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.navBottomBar.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // --- Lógica de Recebimento e Carregamento de Dados ---

        val livroPrincipalId = intent.getStringExtra("LIVRO_PRINCIPAL_ID")

        if (livroPrincipalId != null && livroPrincipalId.isNotBlank()) {
            carregarDetalhesDoLivroPorId(livroPrincipalId)
        } else {
            var listaRecebidaExemplares: ArrayList<booki>? = null

            // Tentativa 1: JSON
            val jsonExemplares = intent.getStringExtra("LISTA_EXEMPLARES_JSON")
            if (!jsonExemplares.isNullOrBlank()) {
                val type = object : TypeToken<ArrayList<booki>>() {}.type
                listaRecebidaExemplares = Gson().fromJson(jsonExemplares, type)
            }

            // Tentativa 2: Parcelable (Legacy/Compatibilidade)
            if (listaRecebidaExemplares == null) {
                listaRecebidaExemplares = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra("LISTA_EXEMPLARES", booki::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra("LISTA_EXEMPLARES")
                }
            }

            if (listaRecebidaExemplares != null && listaRecebidaExemplares.isNotEmpty()) {
                listaExemplaresMutavel.clear()
                listaExemplaresMutavel.addAll(listaRecebidaExemplares)
                livroPrincipal = listaExemplaresMutavel.first()
                popularDadosCardPrincipal(livroPrincipal!!)
                setupRecyclerViewExemplares()
            } else {
                Toast.makeText(this, "Erro: Nenhum dado de livro para exibir.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        // Carregar a lista completa de livros (para busca, etc.)
        var listaRecebidaCompleta: ArrayList<booki>? = null
        val jsonCompleta = intent.getStringExtra("LISTA_COMPLETA_LIVROS_JSON")
        if (!jsonCompleta.isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<booki>>() {}.type
            listaRecebidaCompleta = Gson().fromJson(jsonCompleta, type)
        }
        
        // Fallback para Parcelable da lista completa também
        if (listaRecebidaCompleta == null) {
             listaRecebidaCompleta = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("LISTA_COMPLETA_LIVROS", booki::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra("LISTA_COMPLETA_LIVROS")
            }
        }

        listaRecebidaCompleta?.let { listaCompletaLivros.addAll(it) }

        setupClickListeners()
    }

    private fun carregarDetalhesDoLivroPorId(exemplarId: String) {
        db.collection("livros")
            .document(exemplarId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val exemplarBase = documentSnapshot.toObject(booki::class.java)
                if (exemplarBase != null) {
                    // Garante que o ID esteja setado se não veio automático (embora @DocumentId resolva)
                    if (exemplarBase.id.isBlank()) exemplarBase.id = documentSnapshot.id
                    
                    db.collection("livros")
                        .whereEqualTo("nome", exemplarBase.nome)
                        .whereEqualTo("autor", exemplarBase.autor)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val todosExemplaresDoLivro = querySnapshot.documents.mapNotNull { doc ->
                                val b = doc.toObject(booki::class.java)
                                b?.id = doc.id // Reforço de segurança
                                b
                            }
                            if (todosExemplaresDoLivro.isNotEmpty()) {
                                listaExemplaresMutavel.clear()
                                listaExemplaresMutavel.addAll(todosExemplaresDoLivro)
                                livroPrincipal = listaExemplaresMutavel.first()
                                popularDadosCardPrincipal(livroPrincipal!!)
                                setupRecyclerViewExemplares()
                            } else {
                                Toast.makeText(this, "Nenhum exemplar relacionado encontrado para o livro.", Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao buscar exemplares relacionados: ${e.message}", Toast.LENGTH_LONG).show()
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Livro não encontrado no Firestore (ID: $exemplarId).", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar livro: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
    }

    private fun popularDadosCardPrincipal(livro: booki) {
        Glide.with(this)
            .load(livro.capa)
            .placeholder(R.drawable.logo_unifor)
            .error(R.drawable.logo_unifor)
            .into(binding.imageViewBookCover)

        binding.textViewTitle.text = livro.nome
        binding.textViewAuthor.text = livro.autor
        binding.textViewDetails.text = "Detalhes: Editora - ${livro.editora}"
    }

    private fun setupRecyclerViewExemplares() {
        binding.recyclerViewExemplares.layoutManager = LinearLayoutManager(this)
        exemplarAdapter = ExemplariAdapter(listaExemplaresMutavel) { exemplarParaExcluir: booki ->
            mostrarDialogoExcluirExemplar(exemplarParaExcluir)
        }
        binding.recyclerViewExemplares.adapter = exemplarAdapter
    }

    private fun setupClickListeners() {
        binding.textInputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                irParaMainActivity22()
                true
            } else {
                false
            }
        }

        binding.imageButtonEdit.setOnClickListener {
            livroPrincipal?.let { irParaMainActivity24(it) }
        }
        binding.imageButtonDelete.setOnClickListener {
            livroPrincipal?.let { mostrarDialogoExcluirLivro(it) }
        }
        binding.buttonAddExemplar.setOnClickListener {
            livroPrincipal?.let { irParaMainActivity23(it) }
        }

        binding.navBottomBar.findViewById<View>(R.id.imageButtonHome).setOnClickListener {
            redirectToMainActivity20()
        }
        binding.navBottomBar.findViewById<View>(R.id.imageButtonBooks).setOnClickListener {
            finish()
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
    }

    private fun deleteExemplarFromFirestore(exemplar: booki) {
        if (exemplar.id.isBlank()) {
            Toast.makeText(this, "Erro: ID do exemplar não pode ser vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("livros").document(exemplar.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Exemplar deletado do Firestore!", Toast.LENGTH_SHORT).show()

                val posicao = listaExemplaresMutavel.indexOf(exemplar)
                if (posicao != -1) {
                    listaExemplaresMutavel.removeAt(posicao)
                    exemplarAdapter.notifyItemRemoved(posicao)
                    if (posicao < listaExemplaresMutavel.size) {
                        exemplarAdapter.notifyItemRangeChanged(posicao, listaExemplaresMutavel.size - posicao);
                    }
                }

                if (listaExemplaresMutavel.isEmpty()) {
                    redirectToMainActivity20()
                } else {
                    livroPrincipal?.id?.let { carregarDetalhesDoLivroPorId(it) }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao deletar exemplar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteLivroCompletoFromFirestore(livro: booki) {
        if (livro.id.isBlank()) {
            Toast.makeText(this, "Erro: ID do livro não pode ser vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("livros")
            .whereEqualTo("nome", livro.nome)
            .whereEqualTo("autor", livro.autor)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Todos os exemplares deletados!", Toast.LENGTH_SHORT).show()
                        redirectToMainActivity20()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao deletar exemplares: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar exemplares: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun redirectToMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun mostrarDialogoExcluirLivro(livro: booki) {
        val mensagem = getString(
            R.string.dialogo_excluir_livro_mensagem,
            livro.nome,
            livro.autor,
            livro.editora
        )
        AlertDialog.Builder(this)
            .setTitle(R.string.dialogo_excluir_livro_titulo)
            .setMessage(mensagem)
            .setPositiveButton(R.string.dialogo_botao_excluir) { dialog, _ ->
                deleteLivroCompletoFromFirestore(livro)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialogo_botao_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("StringFormatMatches")
    private fun mostrarDialogoExcluirExemplar(exemplar: booki) {
        val mensagem = getString(R.string.dialogo_excluir_exemplar_mensagem, exemplar.id)

        AlertDialog.Builder(this)
            .setTitle(R.string.dialogo_excluir_exemplar_titulo)
            .setMessage(mensagem)
            .setPositiveButton(R.string.dialogo_botao_excluir) { dialog, _ ->
                deleteExemplarFromFirestore(exemplar)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialogo_botao_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        val textoBusca = binding.textInputEditTextSearch.text.toString()

        intent.putExtra("TEXTO_BUSCA", textoBusca)
        val jsonCompleta = Gson().toJson(listaCompletaLivros)
        intent.putExtra("LISTA_COMPLETA_LIVROS_JSON", jsonCompleta)

        startActivity(intent)
    }

    private fun irParaMainActivity25() {
        startActivity(Intent(this, MainActivity25::class.java))
    }

    private fun irParaMainActivity24(livroParaEditar: booki) {
        val intent = Intent(this, MainActivity24::class.java)
        intent.putExtra("LIVRO_PARA_EDITAR", livroParaEditar)
        startActivity(intent)
    }

    private fun irParaMainActivity23(livroBase: booki) {
        val intent = Intent(this, MainActivity23::class.java)
        intent.putExtra("LIVRO_BASE", livroBase)
        startActivity(intent)
    }

    private fun irParaMainActivity15() {
        startActivity(Intent(this, MainActivity15::class.java))
    }

    private fun irParaMainActivity13() {
        startActivity(Intent(this, MainActivity13::class.java))
    }
}
