package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notifications: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    /**
     * ViewHolder: Esta classe interna "guarda" as referências para os componentes
     * do seu layout (os TextViews, o CardView, etc.) para cada item da lista.
     */
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView as CardView
        val icon: ImageView = itemView.findViewById(R.id.notification_icon)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val description: TextView = itemView.findViewById(R.id.notification_description)
        val time: TextView = itemView.findViewById(R.id.notification_time)
    }

    /**
     * Chamado quando a RecyclerView precisa de um novo item.
     * Ele "infla" (cria) o seu layout item_notification.xml na memória.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    /**
     * Chamado para conectar os dados de uma notificação específica à sua representação visual.
     * É aqui que a mágica acontece.
     * ESTE MÉTODO FOI ATUALIZADO.
     */
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        // 1. LÓGICA DE ÍCONE CORRIGIDA - Usa ícones que EXISTEM
        val iconResource = when (notification.tipo) {
            "ALUGUEL", "RENOVACAO" -> android.R.drawable.ic_menu_info_details // Ícone padrão do Android
            else -> android.R.drawable.ic_dialog_info // Ícone padrão do Android
        }
        holder.icon.setImageResource(iconResource)

        // 2. Preenche os dados
        holder.title.text = notification.titulo
        holder.description.text = notification.mensagem
        holder.time.text = notification.time ?: "Agora" // Fallback seguro

        // 3. Lógica de cor
        if (notification.isRead) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
        } else {
            val highlightColor = ContextCompat.getColor(holder.itemView.context, R.color.notification_unread)
            holder.cardView.setCardBackgroundColor(highlightColor)
        }

        // 4. Lógica de clique
        holder.itemView.setOnClickListener {
            if (!notification.isRead) {
                notification.isRead = true
                notifyItemChanged(position)
            }
        }
    }

    /**
     * Informa à RecyclerView quantos itens existem na lista.
     */
    override fun getItemCount() = notifications.size
}
