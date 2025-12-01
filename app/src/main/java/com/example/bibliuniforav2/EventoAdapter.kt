package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliuniforav2.databinding.ItemEventoBinding
// ✅ REMOVER importações desnecessárias de Date/SimpleDateFormat
// import java.text.SimpleDateFormat
// import java.util.Locale
// import java.util.Date

class EventoAdapter(
    private val onItemClick: (Evento) -> Unit
) : ListAdapter<Evento, EventoAdapter.EventoViewHolder>(EventoDiffCallback()) {

    class EventoViewHolder(private val binding: ItemEventoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(evento: Evento, onItemClick: (Evento) -> Unit) {
            val context = binding.root.context

            // 1. TÍTULO e DESCRIÇÃO
            binding.textViewTituloEvento.text = evento.titulo
            binding.textViewDescricao.text = evento.descricao

            // --- TRATAMENTO DO Date/Timestamp CORRIGIDO ---
            // ✅ Usa as Strings 'data' e 'hora' diretamente do objeto Evento
            val dataFormatada = evento.data
            val horaFormatada = evento.hora

            // 2. DATA e HORA (Usando os campos String)
            binding.textViewData.text = context.getString(R.string.formato_data_evento, dataFormatada)
            binding.textViewHora.text = context.getString(R.string.formato_hora_evento, horaFormatada)

            // 3. LOCAL
            binding.textViewLocal.text = context.getString(R.string.formato_local_evento, evento.local)

            // Configura o listener de clique para o item completo
            binding.root.setOnClickListener {
                onItemClick(evento)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class EventoDiffCallback : DiffUtil.ItemCallback<Evento>() {
        override fun areItemsTheSame(oldItem: Evento, newItem: Evento): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Evento, newItem: Evento): Boolean {
            return oldItem == newItem
        }
    }
}