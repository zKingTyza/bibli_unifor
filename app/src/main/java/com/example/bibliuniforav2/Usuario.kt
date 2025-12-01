package com.example.bibliuniforav2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val matricula: String = "",
    val cpf: String = "",
    val perfil: String = "",
    val usuario: String = ""
) : Parcelable