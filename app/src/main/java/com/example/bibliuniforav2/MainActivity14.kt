package com.example.bibliuniforav2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity14 : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var layoutMessages: LinearLayout
    private lateinit var scrollViewChat: ScrollView
    private lateinit var toolbar: MaterialToolbar

    // ✅ Agora no mesmo package - sem "service"
    private val geminiService = GeminiChatService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main14)

        // Configurar toolbar
        toolbar = findViewById(R.id.toolbar_chat)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Inicializar views do chat
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        layoutMessages = findViewById(R.id.layoutMessages)
        scrollViewChat = findViewById(R.id.scrollViewChat)

        // Mensagem de boas-vindas
        addMessageToChat("🤖 Assistente IA", "Olá! Sou seu assistente da biblioteca. Como posso ajudar?", false)

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Adiciona mensagem do usuário
                addMessageToChat("Você", messageText, true)
                editTextMessage.text.clear()

                // Resposta da IA
                CoroutineScope(Dispatchers.Main).launch {
                    addMessageToChat("🤖 Assistente IA", "Digitando...", false)
                    val aiResponse = geminiService.sendMessage(messageText)

                    // Remove "Digitando..." e adiciona resposta real
                    removeLastMessage()
                    addMessageToChat("🤖 Assistente IA", aiResponse, false)

                    scrollToBottom()
                }
            }
        }
    }

    private fun addMessageToChat(sender: String, message: String, isUser: Boolean) {
        val textView = TextView(this)
        textView.text = "$sender: $message"
        textView.setPadding(24, 12, 24, 12)
        textView.textSize = 16f

        if (isUser) {
            // Mensagem do usuário - azul
            textView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            // Alinhar à direita
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(100, 8, 0, 8)
            }
        } else {
            // Mensagem da IA - cinza
            textView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            // Alinhar à esquerda
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 100, 8)
            }
        }

        layoutMessages.addView(textView)
        scrollToBottom()
    }

    private fun removeLastMessage() {
        if (layoutMessages.childCount > 0) {
            layoutMessages.removeViewAt(layoutMessages.childCount - 1)
        }
    }

    private fun scrollToBottom() {
        scrollViewChat.post { scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}