package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// ✅ REMOVER importação de java.util.Date e SimpleDateFormat, pois agora são Strings
// import java.text.SimpleDateFormat
// import java.util.Locale
// import java.util.Date

class AdminEventAdapter(
    private var eventos: List<Evento>,
    private val onEventAction: (Evento, EventAction) -> Unit // Callback para ações
) : RecyclerView.Adapter<AdminEventAdapter.EventViewHolder>() {

    enum class EventAction {
        EDIT, DELETE
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvEventDateTime: TextView = itemView.findViewById(R.id.tvEventDateTime)
        val tvEventLocal: TextView = itemView.findViewById(R.id.tvEventLocal)
        val btnEditEvent: ImageButton = itemView.findViewById(R.id.btnEditEvent)
        val btnDeleteEvent: ImageButton = itemView.findViewById(R.id.btnDeleteEvent)

        // ✅ REMOVER o SimpleDateFormat, pois agora as datas são Strings separadas
        // private val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

        fun bind(evento: Evento) {
            tvEventTitle.text = evento.titulo

            // ✅ CORREÇÃO AQUI: Combinar 'data' e 'hora' do evento (que são Strings)
            // Você pode formatar como desejar. Ex: "21/11/25 às 22:00"
            val dateTimeCombined = "${evento.data} às ${evento.hora}"
            tvEventDateTime.text = dateTimeCombined // Usa a string combinada

            tvEventLocal.text = evento.local

            btnEditEvent.setOnClickListener {
                onEventAction(evento, EventAction.EDIT)
            }
            btnDeleteEvent.setOnClickListener {
                onEventAction(evento, EventAction.DELETE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_admin_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventos[position])
    }

    override fun getItemCount(): Int = eventos.size

    fun updateEvents(newEvents: List<Evento>) {
        this.eventos = newEvents
        notifyDataSetChanged()
    }
}