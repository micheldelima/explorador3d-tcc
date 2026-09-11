package com.example.explorador3d_hiro_projeto_final.utils

import android.content.Context
import android.widget.Toast

object AppConfig {

    data class Config(
        val idioma: String,
        val somAtivo: Boolean,
        val tamanhoFonte: Int
    )

    private const val PREFS_NAME = "config_app"

    /** Carrega configurações salvas ou aplica padrões iniciais */
    fun carregar(context: Context): Config {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Caso seja o primeiro acesso, salva padrões
        if (!prefs.contains("idioma")) {
            prefs.edit()
                .putString("idioma", "pt")
                .putBoolean("som", true)
                .putInt("tamanho_fonte", 20)
                .apply()
        }

        val idioma = prefs.getString("idioma", "pt") ?: "pt"
        val somAtivo = prefs.getBoolean("som", true)
        val tamanhoFonte = prefs.getInt("tamanho_fonte", 20).coerceIn(12, 40)

        return Config(idioma, somAtivo, tamanhoFonte)
    }

    /** Atualiza um valor específico das preferências */
    fun atualizar(context: Context, chave: String, valor: Any) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (valor) {
            is String -> editor.putString(chave, valor)
            is Boolean -> editor.putBoolean(chave, valor)
            is Int -> editor.putInt(chave, valor)
            else -> Toast.makeText(context, "Tipo inválido para salvar: ${valor::class.java}", Toast.LENGTH_SHORT).show()
        }

        editor.apply()
    }

    /** Aplica idioma em texto simples (mensagens do app, rótulos etc.) */
    fun traduz(textoPt: String, textoEn: String, idioma: String): String {
        return if (idioma == "en") textoEn else textoPt
    }

    /** TTS */
    fun falar(context: Context, somAtivo: Boolean, texto: String, idioma: String) {
        if (!somAtivo) {
            Toast.makeText(
                context,
                if (idioma == "en") "Sound is disabled" else "Som desativado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
