package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliuniforav2.databinding.ItemLivroBinding

// 1. Modifique o construtor para aceitar a "função de clique"
class LivroAdapter(
    private val listaLivros: List<Livro>, // Recebe a lista ÚNICA de livros
    private val onItemClick: (Livro) -> Unit // Função que será chamada no clique
) :
    RecyclerView.Adapter<LivroAdapter.LivroViewHolder>() {

    inner class LivroViewHolder(private val binding: ItemLivroBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 2. Modifique o 'bind'
        fun bind(livro: Livro) {
            binding.textViewTitulo.text = livro.titulo
            binding.textViewAutor.text = livro.autor
            binding.imageViewCapa.setImageResource(livro.capaUrl)

            // 3. Adicione o listener de clique ao card inteiro
            binding.root.setOnClickListener {
                onItemClick(livro) // Chama a função, passando o livro clicado
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLivroBinding.inflate(inflater, parent, false)
        return LivroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        val livro = listaLivros[position]
        holder.bind(livro)
    }

    override fun getItemCount(): Int {
        return listaLivros.size
    }
}