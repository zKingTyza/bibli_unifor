package com.example.bibliuniforav2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity33 : BaseActivity() {

    // Declaração das variáveis que vamos usar na tela
    private lateinit var eventosAdapter: EventoAdapter
    private lateinit var eventosRepository: EventosRepository
    private lateinit var recyclerView: RecyclerView
    // private lateinit var progressBar: ProgressBar // Descomente se adicionar uma barra de progresso no XML

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main33)

        // 1. Configura a barra de ferramentas (Toolbar) e a seta de voltar
        setupToolbar()

        // 2. Prepara a RecyclerView e o Adapter
        setupRecyclerView()

        // 3. Busca e exibe os eventos do Firebase
        fetchAndDisplayEvents()
    }

    /**
     * Configura a Toolbar e a ação do botão de voltar.
     */
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_eventos)
        toolbar.setNavigationOnClickListener {
            finish() // Fecha a tela atual e volta para a anterior
        }
    }

    /**
     * Prepara a RecyclerView, o LayoutManager e o Adapter.
     */
    private fun setupRecyclerView() {
        // Encontra a RecyclerView no layout
        recyclerView = findViewById(R.id.recycler_view_eventos)

        // Instancia o nosso EventoAdapter.
        // A ação de clique por enquanto só mostra um Toast com o título do evento.
        eventosAdapter = EventoAdapter { evento ->
            // Ação que acontece ao clicar em um item da lista
            Toast.makeText(this, "Evento clicado: ${evento.titulo}", Toast.LENGTH_SHORT).show()
        }

        // Conecta a RecyclerView ao Adapter e define o gerenciador de layout
        recyclerView.adapter = eventosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Usa o Repository para buscar os eventos do Firebase e os exibe na tela.
     */
    private fun fetchAndDisplayEvents() {
        // Instancia o repositório, passando a conexão com o Firestore
        val firestore = FirebaseFirestore.getInstance()
        eventosRepository = EventosRepository(firestore)

        // progressBar.visibility = View.VISIBLE // Mostra o loading (se existir)

        // Inicia uma coroutine no escopo do ciclo de vida da Activity
        lifecycleScope.launch {
            try {
                // Chama a função SUSPENSA do repositório para buscar os eventos
                val listaDeEventos = eventosRepository.getAllEventos()

                // Atualiza o adapter com a nova lista de eventos
                eventosAdapter.submitList(listaDeEventos)

                // Log para confirmar que os eventos foram carregados
                Log.d("MainActivity33", "Sucesso! ${listaDeEventos.size} eventos carregados.")

            } catch (e: Exception) {
                // Em caso de erro na busca, mostra um log e um Toast
                Log.e("MainActivity33", "Erro ao buscar eventos", e)
                Toast.makeText(this@MainActivity33, "Falha ao carregar eventos", Toast.LENGTH_LONG).show()
            } finally {
                // progressBar.visibility = View.GONE // Esconde o loading (se existir)
            }
        }
    }
}
