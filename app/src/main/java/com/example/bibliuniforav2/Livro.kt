package com.example.bibliuniforav2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Livro(
    val titulo: String,
    val autor: String,
    val capaUrl:Int,  // Pode ser uma URL da web.
    val sinopse: String,
    val codigo: String,
    val ano: String,
    val editora: String,
    val edicao: String,
    val status : String

    // Se for uma imagem local (drawable), mude o tipo para Int.
) : Parcelable