package com.example.bibliuniforav2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
// ✅ REMOVER importação de java.util.Date se não for mais usada diretamente
// import java.util.Date
import java.util.Calendar // ✅ Manter para Date/Time Picker
import java.util.Locale

class MainActivity12 : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val eventosRepository = EventosRepository(db)

    private lateinit var editTitulo: TextInputEditText
    private lateinit var editDataHora: TextInputEditText
    private lateinit var editLocal: TextInputEditText
    private lateinit var editDescricao: TextInputEditText
    private lateinit var btnSalvar: Button
    private lateinit var btnDeletar: ImageButton

    private var eventoAtual: Evento? = null
    private val calendar = Calendar.getInstance() // ✅ Adicionar Calendar para Date/Time Picker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main12)

        setupViews()
        loadEventoData()
        setupListeners()
        setupDateTimePickers() // ✅ Configurar o Date/Time Picker

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        editTitulo = findViewById(R.id.editTextTitulo)
        editDataHora = findViewById(R.id.editTextDataHora)
        editLocal = findViewById(R.id.editTextLocal)
        editDescricao = findViewById(R.id.editTextDescricao)

        btnSalvar = findViewById(R.id.button5)
        btnDeletar = findViewById(R.id.imageButton6)

        btnSalvar.text = "Salvar Alterações"
    }

    private fun loadEventoData() {
        val eventoRecebido = intent.getParcelableExtra<Evento>("EVENTO_A_EDITAR")

        if (eventoRecebido != null) {
            eventoAtual = eventoRecebido

            editTitulo.setText(eventoRecebido.titulo)
            editLocal.setText(eventoRecebido.local)
            editDescricao.setText(eventoRecebido.descricao)

            // ✅ CORREÇÃO AQUI: Combinar evento.data e evento.hora para exibir
            val dataHoraExibicao = "${eventoRecebido.data} ${eventoRecebido.hora}"
            editDataHora.setText(dataHoraExibicao)

            findViewById<TextView>(R.id.textView20).text = "Editar: ${eventoRecebido.titulo}"

            // Opcional: Parsear a data e hora do evento para o calendar para que o Date/Time Picker comece com o valor atual
            try {
                val combinedDateTimeString = "${eventoRecebido.data} ${eventoRecebido.hora}"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val parsedDate = dateFormat.parse(combinedDateTimeString)
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            } catch (e: Exception) {
                // Logar ou lidar com o erro de parsing se as strings não estiverem no formato esperado

            }

        } else {
            Toast.makeText(this, "Erro: Evento não encontrado para edição.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            finish()
        }

        btnDeletar.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        btnSalvar.setOnClickListener {
            showUpdateConfirmationDialog()
        }
    }

    // ✅ NOVO: Configuração para o Date/Time Picker
    private fun setupDateTimePickers() {
        editDataHora.setOnClickListener {
            showDatePicker()
        }
        editDataHora.keyListener = null // Impede teclado
        editDataHora.isFocusable = false // Impede foco
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeEditText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        editDataHora.setText(dateFormat.format(calendar.time))
    }


// --------------------------------------------------------------------------
// LÓGICA DE CONFIRMAÇÃO E ATUALIZAÇÃO
// --------------------------------------------------------------------------

    private fun showUpdateConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Alterações")
            .setMessage("Tem certeza que deseja salvar as alterações neste evento?")
            .setPositiveButton("Sim") { dialog, which ->
                updateEvento()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun updateEvento() {
        val currentEvento = eventoAtual ?: return

        val novoTitulo = editTitulo.text.toString().trim()
        val novoLocal = editLocal.text.toString().trim()
        val novaDescricao = editDescricao.text.toString().trim()
        val novaDataHoraCompletaStr = editDataHora.text.toString().trim() // Ex: "21/11/2025 22:00"

        if (novoTitulo.isBlank() || novoLocal.isBlank() || novaDescricao.isBlank() || novaDataHoraCompletaStr.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val dataFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val horaFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val novaDataString: String
        val novaHoraString: String
        try {
            val parsedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(novaDataHoraCompletaStr)
            if (parsedDateTime != null) {
                novaDataString = dataFormat.format(parsedDateTime)
                novaHoraString = horaFormat.format(parsedDateTime)
            } else {
                Toast.makeText(this, "Formato de data/hora inválido. Use dd/MM/AAAA HH:MM.", Toast.LENGTH_LONG).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro de formato de data/hora. Use dd/MM/yyyy HH:MM.", Toast.LENGTH_LONG).show()
            return
        }

        // ✅ CORREÇÃO AQUI: Use 'copy' para criar um novo Evento com os campos atualizados
        val eventoAtualizado = currentEvento.copy(
            // id = "" // ✅ NÃO setar o ID como vazio aqui, use o ID existente do eventoAtual
            // O ID já está no 'currentEvento'. Ao usar .copy, ele mantém o ID a menos que você o sobrescreva.
            // Para garantir, você pode explicitamente: id = currentEvento.id
            // Mas o padrão do .copy é manter os campos não especificados.
            titulo = novoTitulo,
            data = novaDataString, // ✅ CORRIGIDO: Passando a String 'data'
            hora = novaHoraString, // ✅ CORRIGIDO: Passando a String 'hora'
            local = novoLocal,
            descricao = novaDescricao
        )

        lifecycleScope.launch {
            val sucesso = eventosRepository.updateEvento(eventoAtualizado)
            if (sucesso) {
                Toast.makeText(this@MainActivity12, "Evento atualizado com sucesso!", Toast.LENGTH_LONG).show()
                navigateToEventDetailsScreen(eventoAtualizado.id)
            } else {
                Toast.makeText(this@MainActivity12, "Falha ao atualizar o evento no BD.", Toast.LENGTH_LONG).show()
            }
        }
    }

// --------------------------------------------------------------------------
// LÓGICA DE CONFIRMAÇÃO E EXCLUSÃO
// --------------------------------------------------------------------------

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
        val currentEvento = eventoAtual ?: return

        // ✅ Corrigir aqui: o ID na data class Evento é String, não String?. Verifique se não está vazio.
        if (currentEvento.id.isNotEmpty()) {
            lifecycleScope.launch {
                val sucesso = eventosRepository.deleteEvento(currentEvento.id)
                if (sucesso) {
                    Toast.makeText(this@MainActivity12, "Evento deletado com sucesso!", Toast.LENGTH_LONG).show()
                    navigateToEventListScreen()
                } else {
                    Toast.makeText(this@MainActivity12, "Falha ao deletar o evento no BD.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "ID do evento ausente ou inválido.", Toast.LENGTH_SHORT).show()
        }
    }

// --------------------------------------------------------------------------
// LÓGICA DE NAVEGAÇÃO
// --------------------------------------------------------------------------

    private fun navigateToEventDetailsScreen(eventoId: String) { // ✅ ID não pode ser nulo aqui
        val intent = Intent(this, MainActivity10::class.java)
        intent.putExtra("EVENTO_ID", eventoId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun navigateToEventListScreen() {
        val intent = Intent(this, MainActivity13::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity20() {
        val intent = Intent(this, MainActivity20::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}