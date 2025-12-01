package com.example.bibliuniforav2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
// ✅ NENHUM IMPORT DE BOOK AQUI, SOMENTE booki

// ✅ Importa o binding correto para item_exemplari.xml
import com.example.bibliuniforav2.databinding.ItemExemplariBinding // <--- CORREÇÃO AQUI!

class ExemplariAdapter(
    // ✅ A lista de exemplares agora é imutável (List<booki>) para consistência
    private val listaDeExemplares: List<booki>,
    private val onDeleteClick: (booki) -> Unit
) : RecyclerView.Adapter<ExemplariAdapter.ExemplarViewHolder>() {

    /**
     * ViewHolder que segura as views de cada item (usando View Binding).
     */
    inner class ExemplarViewHolder(private val binding: ItemExemplariBinding) : // <--- CORREÇÃO AQUI!
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Conecta os dados de um Exemplar (booki) às views do layout
         */
        @SuppressLint("StringFormatInvalid")
        fun bind(exemplar: booki) {
            val context = binding.root.context

            binding.textViewTituloLivro.text = exemplar.nome
            binding.textViewAutorLivro.text = exemplar.autor

            // Exibe o ID do exemplar na nova TextView dedicada
            binding.textViewExemplarId.text = context.getString(
                R.string.formato_id_exemplar, // ✅ Certifique-se que você tem este String resource
                exemplar.id
            )

            binding.textViewExemplarQuantidade.text = String.format(
                context.getString(R.string.formato_quantidade_exemplares), // ✅ Certifique-se que você tem este String resource
                exemplar.quantidade
            )

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
        // ✅ Infla o binding correto para item_exemplari.xml
        val binding = ItemExemplariBinding.inflate(inflater, parent, false) // <--- CORREÇÃO AQUI!
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

    /**
     * Método para atualizar a lista de exemplares no adaptador.
     * Como a lista 'listaDeExemplares' é imutável, este método deve
     * ser chamado para criar um NOVO adaptador ou passar uma nova lista.
     * Ou, se você realmente quer que o adaptador mude sua lista interna,
     * 'listaDeExemplares' deveria ser um 'var' e um 'MutableList'.
     * Para este cenário, estou removendo a lógica interna de 'updateList'
     * e assumindo que você chamará 'notifyDataSetChanged()' após atualizar
     * a lista externa (e reatribuir 'listaDeExemplares' se ela for 'var').
     * Geralmente, é mais limpo criar um novo adapter ou usar ListAdapter/DiffUtil.
     */
    // ✅ Removido o método 'updateList' para simplificar,
    // pois a lista passada para o adapter é 'List<booki>' (imutável).
    // Se você precisa atualizar, passe uma nova lista para o construtor ou
    // use notifyDataSetChanged() se 'listaDeExemplares' fosse um 'var MutableList'.
    // Para um adapter com lista imutável, o ideal é criar um novo adapter ou usar DiffUtil.
}