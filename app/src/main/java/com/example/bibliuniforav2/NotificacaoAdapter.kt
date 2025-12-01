package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliuniforav2.databinding.ItemNotificacaoEmprestimoBinding
import com.example.bibliuniforav2.databinding.ItemNotificacaoSistemaBinding
import com.example.bibliuniforav2.databinding.ItemNotificacaoTardiaBinding

// Define os tipos de view
private const val TIPO_EMPRESTIMO = 1
private const val TIPO_SISTEMA = 2
private const val TIPO_TARDIA = 3

class NotificacaoAdapter(
    // Listeners para os botões do item de empréstimo
    private val onAprovarClick: (Notificacao.Emprestimo) -> Unit,
    private val onCancelarClick: (Notificacao.Emprestimo) -> Unit
) : ListAdapter<Notificacao, RecyclerView.ViewHolder>(NotificacaoDiffCallback()) {

    // --- ViewHolders ---

    // ViewHolder 1: Empréstimo (com cliques)
    inner class EmprestimoViewHolder(private val binding: ItemNotificacaoEmprestimoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notificacao.Emprestimo) {
            binding.imageViewCapa.setImageResource(item.capaUrl)
            binding.textViewDetalhes.text = item.detalhes

            binding.buttonAprovar.setOnClickListener { onAprovarClick(item) }
            binding.buttonCancelar.setOnClickListener { onCancelarClick(item) }
        }
    }

    // ViewHolder 2: Sistema
    class SistemaViewHolder(private val binding: ItemNotificacaoSistemaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notificacao.Sistema) {
            binding.textViewNotificacaoTitulo.text = item.titulo
            binding.textViewDetalhes.text = item.detalhes
        }
    }

    // ViewHolder 3: Tardia
    class TardiaViewHolder(private val binding: ItemNotificacaoTardiaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notificacao.Tardia) {
            binding.textViewNotificacaoTitulo.text = item.titulo
            binding.textViewDetalhes.text = item.detalhes
        }
    }

    // --- Métodos Principais do Adapter ---

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Notificacao.Emprestimo -> TIPO_EMPRESTIMO
            is Notificacao.Sistema -> TIPO_SISTEMA
            is Notificacao.Tardia -> TIPO_TARDIA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TIPO_EMPRESTIMO -> {
                val binding = ItemNotificacaoEmprestimoBinding.inflate(inflater, parent, false)
                EmprestimoViewHolder(binding)
            }
            TIPO_SISTEMA -> {
                val binding = ItemNotificacaoSistemaBinding.inflate(inflater, parent, false)
                SistemaViewHolder(binding)
            }
            TIPO_TARDIA -> {
                val binding = ItemNotificacaoTardiaBinding.inflate(inflater, parent, false)
                TardiaViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Tipo de View inválido")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmprestimoViewHolder -> holder.bind(getItem(position) as Notificacao.Emprestimo)
            is SistemaViewHolder -> holder.bind(getItem(position) as Notificacao.Sistema)
            is TardiaViewHolder -> holder.bind(getItem(position) as Notificacao.Tardia)
        }
    }

    // --- DiffUtil ---
    class NotificacaoDiffCallback : DiffUtil.ItemCallback<Notificacao>() {
        override fun areItemsTheSame(oldItem: Notificacao, newItem: Notificacao): Boolean {
            val oldId = when (oldItem) {
                is Notificacao.Emprestimo -> oldItem.id
                is Notificacao.Sistema -> oldItem.id
                is Notificacao.Tardia -> oldItem.id
            }
            val newId = when (newItem) {
                is Notificacao.Emprestimo -> newItem.id
                is Notificacao.Sistema -> newItem.id
                is Notificacao.Tardia -> newItem.id
            }
            return oldId == newId
        }

        override fun areContentsTheSame(oldItem: Notificacao, newItem: Notificacao): Boolean {
            return oldItem == newItem
        }
    }
}