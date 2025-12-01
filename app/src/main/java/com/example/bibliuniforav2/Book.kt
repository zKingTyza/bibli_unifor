package com.example.bibliuniforav2

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties // <-- 1. IMPORTE A CLASSE
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

// --- 2. ADICIONE ESTA ANOTAÇÃO ---
// Ela instrui o Firestore a ignorar quaisquer campos no documento
// que não existam como propriedades nesta classe (como o campo "stability").
@IgnoreExtraProperties
@Parcelize
data class Book(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("nome") @set:PropertyName("nome") var nome: String = "",
    @get:PropertyName("autor") @set:PropertyName("autor") var autor: String = "",
    @get:PropertyName("capa") @set:PropertyName("capa") var capa: String = "",
    @get:PropertyName("quantidade") @set:PropertyName("quantidade") var quantidade: Int = 0,
    @get:PropertyName("dataEmprestimo") @set:PropertyName("dataEmprestimo") var dataEmprestimo: Long = 0,
    @get:PropertyName("dataDevolucao") @set:PropertyName("dataDevolucao") var dataDevolucao: Long = 0
) : Parcelable
