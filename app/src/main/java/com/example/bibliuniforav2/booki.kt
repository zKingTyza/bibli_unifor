package com.example.bibliuniforav2

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class booki(
    // Adicionada a anotação @DocumentId para mapear corretamente o ID do documento
    @DocumentId
    var id: String = "",

    var nome: String = "",
    var autor: String = "",
    var capa: String = "",
    var editora: String = "",
    var quantidade: Int = 0,

    var dataEmprestimo: Long = 0L,
    var dataDevolucao: Long = 0L
) : Parcelable