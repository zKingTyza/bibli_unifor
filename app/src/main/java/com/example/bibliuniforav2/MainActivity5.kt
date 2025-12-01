package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity5 :  BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        // --- Lógica para o botão de voltar na Toolbar ---
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish() // Fecha a tela atual e volta para a anterior
        }

        // --- Lógica para o clique na opção "Acessibilidade" ---
        val accessibilityOption = findViewById<TextView>(R.id.option_accessibility)
        accessibilityOption.setOnClickListener {
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }

        // --- LÓGICA PARA A OPÇÃO "SAIR" ---
        val logoutOption = findViewById<TextView>(R.id.text_logout)
        logoutOption.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // --- LÓGICA ADICIONADA PARA "EDITAR PERFIL" ---

        // 1. Encontra o TextView "Editar Perfil" usando o ID do XML.
        val editProfileOption = findViewById<TextView>(R.id.option_edit_profile)

        // 2. Configura a ação de clique.
        editProfileOption.setOnClickListener {
            // Cria a intenção (Intent) para abrir a MainActivity9.
            val intent = Intent(this, MainActivity9::class.java)

            // Inicia a navegação para a tela de edição de perfil.
            startActivity(intent)
        }

        // Você também tem a opção "Geral", pode adicionar a lógica dela aqui se precisar.
        // val generalOption = findViewById<TextView>(R.id.option_general)
        // generalOption.setOnClickListener { /* Código para a tela "Geral" */ }
    }
}
