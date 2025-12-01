package com.example.bibliuniforav2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bibliuniforav2.databinding.ActivityMain25Binding // Binding da Activity
import java.util.ArrayList // Import necessário

class MainActivity25 : AppCompatActivity() {

    private lateinit var binding: ActivityMain25Binding
    private lateinit var notificacaoAdapter: NotificacaoAdapter

    // Lista de livros (necessária para navegar para a tela 22)
    private var listaLivros = mutableListOf<Livro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain25Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Corrigir padding do EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.navBottomBar.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // Carrega os dados
        carregarLivrosExemplo() // Carrega a lista de livros (para a navbar)
        setupRecyclerView()      // Carrega e exibe as notificações

        setupClickListeners()
    }

    /**
     * Configura o RecyclerView com o adapter de múltiplas views
     */
    private fun setupRecyclerView() {
        // Passa as funções de clique para o adapter
        notificacaoAdapter = NotificacaoAdapter(
            onAprovarClick = { notificacao ->
                Toast.makeText(this, "Aprovado: ${notificacao.detalhes}", Toast.LENGTH_SHORT).show()
                // TODO: Adicionar lógica para remover item
            },
            onCancelarClick = { notificacao ->
                Toast.makeText(this, "Cancelado: ${notificacao.detalhes}", Toast.LENGTH_SHORT).show()
                // TODO: Adicionar lógica para remover item
            }
        )

        binding.recyclerViewNotificacoes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotificacoes.adapter = notificacaoAdapter

        // Envia a lista de exemplo de notificações para o adapter
        notificacaoAdapter.submitList(carregarNotificacoesExemplo())
    }

    /**
     * Carrega a lista principal de livros (necessária para a navbar).
     */
    private fun carregarLivrosExemplo() {
        listaLivros.clear()
        listaLivros.add(Livro("A Arte da Guerra", "Sun Tzu", R.drawable.logo_unifor, "Sinopse...", "789-A", "Séc. V a.C.", "Várias", "N/A", "Disponível"))
        listaLivros.add(Livro("Engenharia de Software", "Ian Sommerville", R.drawable.logo_unifor, "...", "101-A", "2010", "Pearson", "9ª", "Disponível"))
        listaLivros.add(Livro("Código Limpo", "Robert C. Martin", R.drawable.logo_unifor, "...", "112-A", "2008", "Alta Books", "1ª", "Disponível"))
        // ... (etc.)
    }

    /**
     * Cria uma lista de exemplo com diferentes tipos de notificação
     */
    private fun carregarNotificacoesExemplo(): List<Notificacao> {
        return listOf(
            Notificacao.Tardia(
                id = "1",
                titulo = "Devolução Tardia:",
                detalhes = "'As Crônicas de Gelo...' - Fulano - Atraso: 5 dias."
            ),
            Notificacao.Sistema(
                id = "2",
                titulo = "Atualização de Sistema:",
                detalhes = "Novas funcionalidades implementadas."
            ),
            Notificacao.Emprestimo(
                id = "3",
                capaUrl = R.drawable.logo_unifor,
                detalhes = "'Código Limpo' - Ciclano - Devolução: 30/10/2025"
            )
        )
    }

    /**
     * Configura os cliques da barra de navegação
     */
    private fun setupClickListeners() {

        // --- INÍCIO DA CORREÇÃO ---

        // Botão Home: Volta para a MainActivity (a tela principal)
        binding.imageButtonHome.setOnClickListener {
            val intent = Intent(this, MainActivity20::class.java) // Corrigido de MainActivity20
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // Botão Livros: Vai para a tela 22
        binding.imageButtonBooks.setOnClickListener {
            irParaMainActivity22()
        }

        // Botão Perfil: Vai para a tela 15
        binding.imageButtonProfile.setOnClickListener {
            irParaMainActivity15()
        }

        // Botão Eventos: Vai para a tela 13
        binding.imageButtonEvents.setOnClickListener {
            irParaMainActivity13()
        }

        // Botão Notificações: (Já está aqui)
        binding.imageButtonNotifications.setOnClickListener {
            Toast.makeText(this, "Você já está em Notificações", Toast.LENGTH_SHORT).show()
        }

        // --- FIM DA CORREÇÃO ---
    }

    // --- Funções de Navegação ---

    /**
     * Prepara e inicia a MainActivity22 a partir desta tela.
     */
    private fun irParaMainActivity22() {
        val intent = Intent(this, MainActivity22::class.java)
        intent.putExtra("TEXTO_BUSCA", "") // Envia busca vazia
        intent.putParcelableArrayListExtra("LISTA_COMPLETA_LIVROS", ArrayList(listaLivros))
        startActivity(intent)
    }

    /**
     * Inicia a MainActivity13 (Tela de Eventos)
     */
    private fun irParaMainActivity13() {
        // TODO: Crie a MainActivity13 e a registre no Manifesto
        val intent = Intent(this, MainActivity13::class.java)
        startActivity(intent)
    }

    /**
     * Inicia a MainActivity15 (Tela de Perfil)
     */
    private fun irParaMainActivity15() {
        // TODO: Crie a MainActivity15 e a registre no Manifesto
        val intent = Intent(this, MainActivity15::class.java)
        startActivity(intent)
        }
}