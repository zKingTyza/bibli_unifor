package com.example.bibliuniforav2

import java.util.Properties

object Config {
    private const val PROPERTIES_FILE = "local.properties"

    fun getGeminiApiKey(): String {
        return try {
            val properties = Properties()
            val inputStream = javaClass.classLoader?.getResourceAsStream(PROPERTIES_FILE)
            properties.load(inputStream)
            properties.getProperty("gemini.api.key") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}