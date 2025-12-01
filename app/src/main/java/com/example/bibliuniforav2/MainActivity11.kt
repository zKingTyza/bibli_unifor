package com.example.bibliuniforav2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bibliuniforav2.databinding.ActivityMain11Binding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
// ✅ REMOVER importação de java.util.Date se não for mais usada diretamente
// import java.util.Date
import java.util.Locale

class MainActivity11 : AppCompatActivity() {

    private lateinit var binding: ActivityMain11Binding
    private val eventosRepository by lazy { EventosRepository(FirebaseFirestore.getInstance()) }

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain11Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
        setupDateTimePickers()
    }

    private fun setupClickListeners() {
        binding.imageButton4.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.imageButton6.setOnClickListener {
            Toast.makeText(this, "Não é possível deletar um evento que ainda não existe.", Toast.LENGTH_SHORT).show()
        }

        binding.button5.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun setupDateTimePickers() {
        binding.textInputLayout6.editText?.setOnClickListener {
            showDatePicker()
        }
        binding.textInputLayout6.editText?.keyListener = null
        binding.textInputLayout6.editText?.isFocusable = false
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
        // Formato para exibir no campo de texto (ex: "21/11/2025 22:00")
        val combinedFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.textInputLayout6.editText?.setText(combinedFormat.format(calendar.time))
        // NOTA: Se você tiver campos separados para data e hora no XML,
        // você precisaria formatar e setar em campos diferentes aqui.
    }


    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Adição")
            .setMessage("Tem certeza que deseja adicionar este evento?")
            .setPositiveButton("Sim") { dialog, which ->
                addEventToFirestore()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun addEventToFirestore() {
        val titulo = binding.textInputLayout3.editText?.text.toString().trim()
        val dataHoraStringCompleta = binding.textInputLayout6.editText?.text.toString().trim() // Ex: "21/11/2025 22:00"
        val local = binding.textInputLayout7.editText?.text.toString().trim()
        val sobre = binding.textInputLayout9.editText?.text.toString().trim()

        if (titulo.isEmpty() || dataHoraStringCompleta.isEmpty() || local.isEmpty() || sobre.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
            return
        }

        // ✅ CORREÇÃO AQUI: Extraia a data e a hora da string completa
        val dataFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val horaFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dataString: String
        val horaString: String
        try {
            val parsedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(dataHoraStringCompleta)
            if (parsedDate != null) {
                dataString = dataFormat.format(parsedDate)
                horaString = horaFormat.format(parsedDate)
            } else {
                Toast.makeText(this, "Formato de data/hora inválido. Use DD/MM/AAAA HH:MM.", Toast.LENGTH_LONG).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de data/hora inválido. Use DD/MM/AAAA HH:MM.", Toast.LENGTH_LONG).show()
            return
        }

        val novoEvento = Evento(
            id = "", // ✅ CORRIGIDO: O ID é String, não String?. Começa vazio para o Firestore gerar.
            titulo = titulo,
            data = dataString, // ✅ CORRIGIDO: Passando a String 'data'
            hora = horaString, // ✅ CORRIGIDO: Passando a String 'hora'
            local = local,
            descricao = sobre
        )

        lifecycleScope.launch {
            try {
                // Seu EventosRepository.createEvento agora é suspend e retorna String? (o ID)
                val newEventId = eventosRepository.createEvento(novoEvento)

                if (newEventId != null) {
                    Toast.makeText(this@MainActivity11, "Evento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity11, MainActivity10::class.java)
                    intent.putExtra("EVENTO_ID", newEventId)
                    startActivity(intent)
                    finish() // Finaliza esta Activity
                } else {
                    Toast.makeText(this@MainActivity11, "Falha ao adicionar evento.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity11, "Erro ao adicionar evento: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}