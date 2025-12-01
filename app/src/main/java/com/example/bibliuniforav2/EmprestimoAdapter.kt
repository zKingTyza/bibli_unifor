package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date // A importação correta!
import java.util.Locale

class EmprestimoAdapter(
    private val emprestimos: List<Book>,
    private val onRenewClickListener: (Book) -> Unit
) : RecyclerView.Adapter<EmprestimoAdapter.EmprestimoViewHolder>() {

    class EmprestimoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capa: ImageView = itemView.findViewById(R.id.imageViewCapaEmprestimo)
        val titulo: TextView = itemView.findViewById(R.id.textViewTituloEmprestimo)
        val autor: TextView = itemView.findViewById(R.id.textViewAutorEmprestimo)
        val dataDevolucao: TextView = itemView.findViewById(R.id.textViewDataDevolucao)
        val botaoRenovar: Button = itemView.findViewById(R.id.buttonRenovar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmprestimoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emprestimo, parent, false)
        return EmprestimoViewHolder(view)
    }

    override fun getItemCount(): Int = emprestimos.size

    override fun onBindViewHolder(holder: EmprestimoViewHolder, position: Int) {
        val emprestimo = emprestimos[position]
        holder.titulo.text = emprestimo.nome
        holder.autor.text = emprestimo.autor

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataFormatada = sdf.format(Date(emprestimo.dataDevolucao))
        holder.dataDevolucao.text = "Devolver em: $dataFormatada"

        holder.botaoRenovar.setOnClickListener {
            onRenewClickListener(emprestimo)
        }

        Glide.with(holder.itemView.context)
            .load(emprestimo.capa)
            .placeholder(R.drawable.logo_unifor)
            .error(R.drawable.logo_unifor)
            .into(holder.capa)
    }
}
