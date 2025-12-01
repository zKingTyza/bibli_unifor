package com.example.bibliuniforav2

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Data class para representar um Evento no Firestore.
 * Corrigida para corresponder aos campos 'data' e 'hora' como Strings no BD.
 */
@Parcelize
data class Evento(
    // Usar @DocumentId é o padrão e é mais limpo para IDs
    @DocumentId
    var id: String = "",

    @get:PropertyName("titulo") @set:PropertyName("titulo") var titulo: String = "",

    // ✅ CORRIGIDO: Deve ser 'data' e 'hora' (Strings), para corresponder ao BD.
    @get:PropertyName("data") @set:PropertyName("data") var data: String = "",
    @get:PropertyName("hora") @set:PropertyName("hora") var hora: String = "",

    @get:PropertyName("local") @set:PropertyName("local") var local: String = "",
    @get:PropertyName("descricao") @set:PropertyName("descricao") var descricao: String = ""

) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}