package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliuniforav2.Book
import com.example.bibliuniforav2.databinding.ItemExemplarBinding

class ExemplarAdapter(
    private val listaDeExemplares: List<Book>,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<ExemplarAdapter.ExemplarViewHolder>() {

    /**
     * ViewHolder que segura as views de cada item (usando View Binding).
     */
    inner class ExemplarViewHolder(private val binding: ItemExemplarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Conecta os dados de um Exemplar (Book) às views do layout
         */
        fun bind(exemplar: Book) {
            val context = binding.root.context

            // ✅ Lógica de ID: Mapeia o ID do documento
            binding.textViewExemplarId.text = context.getString(
                R.string.formato_id_exemplar,
                // Usando o ID do documento (campo 'id')
                exemplar.id
            )

            // ❌ REMOVIDA: A lógica que fazia referência ao textViewExemplarStatus e exemplar.status
            // O textViewExemplarStatus não existe mais no layout, e o status não é usado.

            // Configura o clique no botão de excluir
            binding.imageButtonDelete.setOnClickListener {
                onDeleteClick(exemplar)
            }
        }
    }

    /**
     * Cria um novo ViewHolder quando a RecyclerView precisa.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExemplarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExemplarBinding.inflate(inflater, parent, false)
        return ExemplarViewHolder(binding)
    }

    /**
     * Vincula os dados do Exemplar da posição correta ao ViewHolder.
     */
    override fun onBindViewHolder(holder: ExemplarViewHolder, position: Int) {
        val exemplar = listaDeExemplares[position]
        holder.bind(exemplar)
    }

    /**
     * Retorna o número total de itens na lista.
     */
    override fun getItemCount(): Int {
        return listaDeExemplares.size
    }
}