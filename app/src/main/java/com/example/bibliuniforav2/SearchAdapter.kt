package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// 1. O CONSTRUTOR FOI ALTERADO AQUI
// Agora ele aceita uma lista de livros E uma função para ser executada no clique.
class SearchAdapter(
    private val bookList: List<Book>,
    private val onBookClickListener: (Book) -> Unit // Este é o "callback" do clique
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    // Esta classe interna representa a View de cada item da lista.
    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capa: ImageView = itemView.findViewById(R.id.imageViewCapaResultado)
        val titulo: TextView = itemView.findViewById(R.id.textViewTituloResultado)
        val autor: TextView = itemView.findViewById(R.id.textViewAutorResultado)
    }

    // Chamado pelo RecyclerView para criar um novo ViewHolder (quando não há um disponível para reutilizar).
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        // "Infla" (cria) o layout do item de resultado da busca.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    // Retorna a quantidade de itens na lista.
    override fun getItemCount(): Int {
        return bookList.size
    }

    // Chamado pelo RecyclerView para conectar (bind) os dados de um livro a um ViewHolder.
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val book = bookList[position]

        // Define o título e o autor.
        holder.titulo.text = book.nome
        holder.autor.text = book.autor

        // 2. UM "OUVINTE DE CLIQUE" FOI ADICIONADO AQUI
        // Quando o item da lista for clicado, ele executa a função 'onBookClickListener'.
        holder.itemView.setOnClickListener {
            onBookClickListener(book)
        }

        // Usa o Glide para carregar a imagem da capa.
        Glide.with(holder.itemView.context)
            .load(book.capa) // Carrega a URL da imagem.
            .placeholder(R.drawable.logo_unifor) // Imagem que aparece enquanto a original carrega.
            .error(R.drawable.logo_unifor) // Imagem que aparece se der erro ao carregar.
            .into(holder.capa) // Onde a imagem será exibida.
    }
}
