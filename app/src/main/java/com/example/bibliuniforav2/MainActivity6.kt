package com.example.bibliuniforav2

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity6 : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    // Opções de fonte definidas aqui para serem reutilizadas
    private val fontOptions = arrayOf("Pequeno", "Médio", "Grande")
    private val fontScales = listOf(0.85f, 1.0f, 1.15f) // MUDANÇA IMPORTANTE: Usando listOf em vez de floatArrayOf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)

        setupToolbar()
        setupDarkModeSwitch()
        setupFontSizeOption()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupDarkModeSwitch() {
        val darkModeSwitch = findViewById<MaterialSwitch>(R.id.switch_dark_mode)
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean("dark_mode", isChecked) }
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupFontSizeOption() {
        val fontSizeOption = findViewById<TextView>(R.id.option_font_size)
        fontSizeOption.setOnClickListener {
            showFontSizeDialog()
        }
    }

    private fun showFontSizeDialog() {
        // Pega a escala atual salva, com 1.0f (Médio) como padrão
        val currentScale = sharedPreferences.getFloat("font_scale", 1.0f)

        // Agora o indexOf funciona perfeitamente porque 'fontScales' é uma Lista.
        // Se não encontrar, assume 1 (Médio) como padrão seguro.
        val currentSelectionIndex = fontScales.indexOf(currentScale).takeIf { it != -1 } ?: 1

        AlertDialog.Builder(this)
            .setTitle("Tamanho da Fonte")
            .setSingleChoiceItems(fontOptions, currentSelectionIndex) { dialog, which ->
                val selectedScale = fontScales[which]

                if (currentScale != selectedScale) {
                    sharedPreferences.edit { putFloat("font_scale", selectedScale) }
                    dialog.dismiss()
                    recreate()
                } else {
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
