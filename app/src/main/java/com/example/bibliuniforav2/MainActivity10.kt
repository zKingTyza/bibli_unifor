package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
// ✅ REMOVER importação de java.text.SimpleDateFormat e java.util.Locale se não for mais usada diretamente
// import java.text.SimpleDateFormat
// import java.util.Locale

class MainActivity10 : AppCompatActivity() {

    // Views para exibir os dados do evento
    private lateinit var tvNomeEvento: TextView
    private lateinit var tvDataEvento: TextView
    private lateinit var tvLocalEvento: TextView
    private lateinit var tvDescricaoEvento: TextView
    private lateinit var btnDeletar: ImageButton

    // Inicialização do Firebase e do Repositório
    private val db = FirebaseFirestore.getInstance()
    private val eventosRepository = EventosRepository(db)

    // Inicialização do ViewModel usando o Factory para injetar o Repositório
    private val viewModel: EventoDetailViewModel by viewModels {
        EventoDetailViewModel.Factory(eventosRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main10)

        // 1. Mapeamento das Views
        tvNomeEvento = findViewById(R.id.textView21)
        tvDataEvento = findViewById(R.id.textView24)
        tvLocalEvento = findViewById(R.id.textView26)
        tvDescricaoEvento = findViewById(R.id.textView28)
        btnDeletar = findViewById(R.id.imageButton6)

        // 2. Observar dados do ViewModel
        observeViewModel()

        // 3. Carregar o Evento
        loadEventoData()

        // 4. Configurar listeners dos botões
        setupActionListeners()
    }

    private fun observeViewModel() {
        viewModel.evento.observe(this) { evento ->
            evento?.let {
                tvNomeEvento.text = it.titulo

                // ✅ CORREÇÃO AQUI: Combine 'data' e 'hora' (que são Strings) para exibição.
                val dataHoraFormatada = "${it.data} às ${it.hora}"
                tvDataEvento.text = dataHoraFormatada // Usa a string combinada

                tvLocalEvento.text = it.local
                tvDescricaoEvento.text = it.descricao

                findViewById<TextView>(R.id.textView20).text = it.titulo
            } ?: run {
                Toast.makeText(this, "Evento não encontrado ou erro ao carregar.", Toast.LENGTH_LONG).show()
                // Opcional: Aqui você pode chamar finish() se o evento for essencial para a tela.
                // finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadEventoData() {
        val eventoId = intent.getStringExtra("EVENTO_ID")

        if (eventoId != null) {
            viewModel.loadEvento(eventoId)
        } else {
            Toast.makeText(this, "ID do evento não fornecido. Fechando tela.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupActionListeners() {
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            val eventoParaEditar = viewModel.evento.value

            if (eventoParaEditar != null) {
                val intent = Intent(this, MainActivity12::class.java)
                // O Evento é Parcelable, pode ser passado diretamente.
                intent.putExtra("EVENTO_A_EDITAR", eventoParaEditar)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Não foi possível carregar o evento para edição.", Toast.LENGTH_SHORT).show()
            }
        }

        btnDeletar.setOnClickListener {
            showDeleteConfirmationDialog()
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
            navigateToEventListScreen()
        }
        findViewById<ImageButton>(R.id.imageButtonNotifications).setOnClickListener {
            irParaMainActivity25()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja DELETAR este evento permanentemente?")
            .setPositiveButton("Sim") { dialog, which ->
                deleteEvento()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteEvento() {
        val currentEventoId = viewModel.evento.value?.id

        if (currentEventoId != null && currentEventoId.isNotEmpty()) { // Adicionado check de .isNotEmpty()
            lifecycleScope.launch {
                val sucesso = eventosRepository.deleteEvento(currentEventoId)
                if (sucesso) {
                    Toast.makeText(this@MainActivity10, "Evento deletado com sucesso!", Toast.LENGTH_LONG).show()
                    navigateToEventListScreen()
                } else {
                    Toast.makeText(this@MainActivity10, "Falha ao deletar o evento no BD.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Não foi possível deletar: ID do evento não encontrado ou é inválido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        startActivity(intent)
    }

    private fun irParaMainActivity25() {
        val intent = Intent(this, MainActivity25::class.java)
        startActivity(intent)
    }

    private fun navigateToEventListScreen() {
        val intent = Intent(this, MainActivity13::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun irParaMainActivity15() {
        val intent = Intent(this, MainActivity15::class.java)
        startActivity(intent)
    }
}