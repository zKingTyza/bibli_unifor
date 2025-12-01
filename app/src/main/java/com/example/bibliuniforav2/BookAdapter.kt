package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// 1. ADICIONAMOS A INTERFACE DE CLIQUE
class BookAdapter(
    // ⚠️ MUDANÇA: 'private var' para permitir a reatribuição da lista
    private var bookList: List<Book>,
    private val onBookClickListener: (Book) -> Unit // <- Interface de callback
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capa: ImageView = itemView.findViewById(R.id.imageViewCapa)
        val titulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        val autor: TextView = itemView.findViewById(R.id.textViewAutor)
    }

    // ✅ NOVO MÉTODO: Permite atualizar a lista de livros após a busca do Firebase
    fun setBooks(newBookList: List<Book>) {
        this.bookList = newBookList
        notifyDataSetChanged() // Notifica o RecyclerView para redesenhar a tela
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        holder.titulo.text = book.nome
        holder.autor.text = book.autor

        // 2. CONFIGURAMOS O CLIQUE NO ITEM INTEIRO
        holder.itemView.setOnClickListener {
            onBookClickListener(book) // Chama a função de callback passando o livro clicado
        }

        Glide.with(holder.itemView.context)
            .load(book.capa)
            .placeholder(R.drawable.logo_unifor)
            .error(R.drawable.logo_unifor)
            .into(holder.capa)
    }
}