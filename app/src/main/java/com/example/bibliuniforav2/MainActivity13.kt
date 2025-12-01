package com.example.bibliuniforav2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity13 : AppCompatActivity() {

    private lateinit var recyclerViewEventosAtivos: RecyclerView
    private lateinit var eventosAdapter: AdminEventAdapter
    private lateinit var viewModel: AdminEventsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main13)

        // 1. Inicializa o ViewModel
        val db = FirebaseFirestore.getInstance()
        val repository = EventosRepository(db)
        viewModel = ViewModelProvider(this, AdminEventsViewModel.Factory(repository))
            .get(AdminEventsViewModel::class.java)

        // 2. Configura a RecyclerView
        recyclerViewEventosAtivos = findViewById(R.id.recyclerViewEventosAtivos)

        recyclerViewEventosAtivos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        eventosAdapter = AdminEventAdapter(emptyList()) { evento, action ->
            when (action) {
                AdminEventAdapter.EventAction.EDIT -> {
                    val intent = Intent(this, MainActivity12::class.java)
                    intent.putExtra("EVENTO_A_EDITAR", evento)
                    startActivity(intent)
                }
                AdminEventAdapter.EventAction.DELETE -> {
                    showDeleteConfirmationDialog(evento)
                }
            }
        }
        recyclerViewEventosAtivos.adapter = eventosAdapter

        // 3. Observa os eventos do ViewModel
        viewModel.eventosAtivos.observe(this) { eventos ->
            eventosAdapter.updateEvents(eventos)
            if (eventos.isEmpty()) {
                Toast.makeText(this, "Nenhum evento ativo encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        // 4. Configura Listeners
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadEventosAtivos()
    }

    private fun setupListeners() {


        // 🚨 CORREÇÃO AQUI: Redireciona para MainActivity11 ao clicar em btnAdicionarTopo
        findViewById<Button>(R.id.btnAdicionarTopo).setOnClickListener {
            val intent = Intent(this, MainActivity11::class.java)
            startActivity(intent)
        }

        // --- BARRA DE NAVEGAÇÃO INFERIOR ---
        findViewById<ImageButton>(R.id.imageButtonHome).setOnClickListener {
            val intent = Intent(this, MainActivity20::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        findViewById<ImageButton>(R.id.imageButtonProfile).setOnClickListener {
            irParaMainActivity15()
        }
        findViewById<ImageButton>(R.id.imageButtonBooks).setOnClickListener {
            irParaMainActivity22()
        }
        findViewById<ImageButton>(R.id.imageButtonEvents).setOnClickListener {
            // Já está na MainActivity13, não faz nada ou rola para o topo
            recyclerViewEventosAtivos.scrollToPosition(0)
        }
        findViewById<ImageButton>(R.id.imageButtonNotifications).setOnClickListener {
            irParaMainActivity25()
        }
        // ... Lógica para o campo de pesquisa (ainda não implementada)
        // findViewById<EditText>(R.id.etPesquisar).addTextChangedListener { }
    }

    private fun showDeleteConfirmationDialog(evento: Evento) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja DELETAR o evento '${evento.titulo}' permanentemente?")
            .setPositiveButton("Sim") { dialog, which ->
                viewModel.deleteEvento(evento.id!!)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    // --------------------------------------------------------------------------
    // FUNÇÕES DE NAVEGAÇÃO (para a barra inferior)
    // --------------------------------------------------------------------------
    private fun irParaMainActivity15() {
        val intent = Intent(this, MainActivity15::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity25() {
        val intent = Intent(this, MainActivity25::class.java)
        startActivity(intent)
    }
}