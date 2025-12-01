package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LivroSearchAdapter(
    private val onItemClick: (Book) -> Unit,
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : ListAdapter<Book, LivroSearchAdapter.BookSearchViewHolder>(BookDiffCallback()) {

    class BookSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Mapeamento das Views do item_livro_search.xml
        val capa: ImageView = itemView.findViewById(R.id.imageViewCapa)
        val titulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        val autor: TextView = itemView.findViewById(R.id.textViewAutor)

        // Se item_livro_search tiver botões de Ação, eles seriam mapeados aqui

        fun bind(book: Book, onItemClick: (Book) -> Unit, onEditClick: (Book) -> Unit, onDeleteClick: (Book) -> Unit) {

            titulo.text = book.nome
            autor.text = book.autor

            Glide.with(itemView.context)
                .load(book.capa)
                .placeholder(R.drawable.logo_unifor)
                .error(R.drawable.logo_unifor)
                .into(capa)

            // Clique no item (para detalhes)
            itemView.setOnClickListener {
                onItemClick(book)
            }

            // Lógica de clique para Delete/Edit (se houver botões no layout de pesquisa)
            // Exemplo: itemView.findViewById<ImageView>(R.id.imageViewDelete)?.setOnClickListener { onDeleteClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewHolder {
        // Infla o layout correto para a pesquisa: item_livro_search.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro_search, parent, false)
        return BookSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookSearchViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book, onItemClick, onEditClick, onDeleteClick)
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            // Usa o ID único para determinar se são o mesmo item (mesmo que o conteúdo mude)
            return oldItem.id == newItem.id
        }

        // ✅ CORRIGIDO: Ambos os parâmetros devem ser do tipo Book
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            // Compara se o conteúdo é o mesmo. Como Book é uma data class, a comparação estrutural funciona.
            return oldItem == newItem
        }
    }
}