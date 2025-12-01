package com.example.bibliuniforav2

// Sealed class para agrupar os diferentes tipos de notificação
sealed class Notificacao {

    // Tipo 1: Notificação de Empréstimo
    data class Emprestimo(
        val id: String,
        val capaUrl: Int, // Campo para a capa do livro
        val detalhes: String
        // O título "Nova Solicitação..." é estático no XML, não é necessário aqui
    ) : Notificacao()

    // Tipo 2: Notificação de Sistema
    data class Sistema(
        val id: String,
        val titulo: String, // O título é dinâmico neste layout
        val detalhes: String
    ) : Notificacao()

    // Tipo 3: Notificação Tardia
    data class Tardia(
        val id: String,
        val titulo: String, // O título é dinâmico neste layout
        val detalhes: String
    ) : Notificacao()
}