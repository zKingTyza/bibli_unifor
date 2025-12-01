package com.example.bibliuniforav2

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Esta é uma classe base da qual todas as outras Activities do app devem herdar.
 * Sua função é aplicar as configurações de acessibilidade (tema e fonte)
 * de forma consistente em todo o aplicativo.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Pega a preferência de tamanho de fonte salva pelo usuário
        val sharedPreferences = newBase.getSharedPreferences("app_settings", MODE_PRIVATE)
        // O valor padrão é 1.0f (médio). 0.85f será pequeno, 1.15f será grande.
        val fontScale = sharedPreferences.getFloat("font_scale", 1.0f)

        // Cria uma nova configuração baseada na configuração atual do contexto
        val config = Configuration(newBase.resources.configuration)
        // Define a escala da fonte nesta nova configuração
        config.fontScale = fontScale

        // Cria um novo contexto com a configuração de fonte atualizada e o aplica à Activity
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aplica o tema (claro/escuro) sempre que uma Activity é criada
        applyTheme()
    }

    private fun applyTheme() {
        val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        // O valor padrão é 'false' (modo claro)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        // Define o modo noturno para todo o aplicativo
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
