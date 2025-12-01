package com.example.bibliuniforav2

import androidx.annotation.DrawableRes
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class Notification(
    // Campos que SÃO salvos no Firebase
    var titulo: String = "",
    var mensagem: String = "",
    var tipo: String = "",

    // 🔑 NOVO CAMPO - Vínculo com o usuário
    var uidAuth: String = "",

    @ServerTimestamp
    var data: Date? = null,

    // --- Campos IGNORADOS pelo Firebase ---

    @get:Exclude
    var id: String = "",

    @get:Exclude
    var isRead: Boolean = false,

    @get:Exclude
    var time: String = "",

    @get:Exclude
    @get:DrawableRes
    val iconResId: Int = 0
)