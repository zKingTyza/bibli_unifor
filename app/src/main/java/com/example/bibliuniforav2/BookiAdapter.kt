package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AlertDialog

class BookiAdapter(
    // ✅ Corrigido para trabalhar com 'booki'
    private var bookList: List<booki>,
    // ✅ Corrigido para trabalhar com 'booki'
    private val onBookClickListener: (booki) -> Unit,
    // ✅ Corrigido para trabalhar com 'booki'
    private val onEditClickListener: ((booki) -> Unit)? = null,
    // ✅ Corrigido para trabalhar com 'booki'
    private val onDeleteClickListener: ((booki) -> Unit)? = null,
    private val useSearchLayout: Boolean = false
) : RecyclerView.Adapter<BookiAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capa: ImageView = itemView.findViewById(R.id.imageViewCapa)
        val titulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        val autor: TextView = itemView.findViewById(R.id.textViewAutor)
        // Estes botões são opcionais e só devem existir em R.layout.item_livro_search
        val editButton: ImageButton? = itemView.findViewById(R.id.imageButtonEdit)
        val deleteButton: ImageButton? = itemView.findViewById(R.id.imageButtonDelete)
    }

    // ✅ Corrigido para aceitar uma lista de 'booki'
    fun setBooks(newBookList: List<booki>) {
        this.bookList = newBookList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        // Seleciona o layout baseado no parâmetro useSearchLayout
        val layoutResId = if (useSearchLayout) R.layout.item_livro_search else R.layout.item_livro
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position] // 'book' é do tipo 'booki' aqui

        holder.titulo.text = book.nome
        holder.autor.text = book.autor

        // Carrega a imagem da capa usando Glide
        Glide.with(holder.itemView.context)
            .load(book.capa)
            .placeholder(R.drawable.logo_unifor) // Imagem de placeholder enquanto carrega
            .error(R.drawable.logo_unifor)     // Imagem de erro se o carregamento falhar
            .into(holder.capa)

        // Configura o clique no item completo do RecyclerView
        holder.itemView.setOnClickListener {
            onBookClickListener(book)
        }

        // Condicionalmente mostra e configura os botões de edição/exclusão
        if (useSearchLayout) {
            holder.editButton?.visibility = View.VISIBLE
            holder.deleteButton?.visibility = View.VISIBLE

            // Configura o clique no botão de edição
            holder.editButton?.setOnClickListener {
                onEditClickListener?.invoke(book) // Invoca o listener se ele não for nulo
            }

            // Configura o clique no botão de exclusão, com uma caixa de diálogo de confirmação
            holder.deleteButton?.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir o livro '${book.nome}'?")
                    .setPositiveButton("Sim") { dialog, _ ->
                        onDeleteClickListener?.invoke(book) // Invoca o listener se ele não for nulo
                        dialog.dismiss()
                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } else {
            // Se não estiver usando o layout de busca, garanta que os botões estão ocultos
            holder.editButton?.visibility = View.GONE
            holder.deleteButton?.visibility = View.GONE
        }
    }
}