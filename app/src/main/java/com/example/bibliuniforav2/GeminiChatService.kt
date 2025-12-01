package com.example.bibliuniforav2

import com.google.ai.client.generativeai.GenerativeModel

class GeminiChatService {
    private var generativeModel: GenerativeModel? = null

    init {
        initializeModel()
    }

    private fun initializeModel() {
        // ✅ API KEY DIRETA AQUI - COLE SUA CHAVE
        val apiKey = "AIzaSyCTBwBb6sBCevLQSr6Xsx03aqrUpuMuHZM" // 👈 COLE SUA CHAVE GEMINI AQUI

        // CORREÇÃO: Remova a comparação redundante.
        // Apenas verifique se a chave não está vazia.
        if (apiKey.isNotEmpty()) {
            generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash", // Recomendo "1.5-flash" que é mais recente
                apiKey = apiKey
            )
        }
    }


    suspend fun sendMessage(userMessage: String): String {
        return try {
            if (generativeModel == null) {
                return "❌ Erro: API Key não configurada. Cole sua chave no código!"
            }

            val response = generativeModel!!.generateContent(userMessage)
            response.text ?: "Desculpe, não consegui gerar uma resposta."

        } catch (e: Exception) {
            "❌ Erro: ${e.message ?: "Falha na comunicação com a IA"}"
        }
    }
}