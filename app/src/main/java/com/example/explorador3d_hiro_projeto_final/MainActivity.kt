package com.example.explorador3d_hiro_projeto_final

import com.example.explorador3d_hiro_projeto_final.utils.AppConfig
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnIniciar: MaterialButton
    private lateinit var btnSobre: MaterialButton
    private lateinit var btnConfig: MaterialButton
    private lateinit var txtDescricao: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referências
        btnIniciar = findViewById(R.id.btnIniciar)
        btnSobre = findViewById(R.id.btnSobre)
        btnConfig = findViewById(R.id.btnConfig)
        txtDescricao = findViewById(R.id.txtDescricao)

        // Ações dos botões
        btnIniciar.setOnClickListener {
            startActivity(Intent(this, ExploracaoActivity::class.java))
        }
        btnSobre.setOnClickListener {
            startActivity(Intent(this, SobreActivity::class.java))
        }
        btnConfig.setOnClickListener {
            startActivity(Intent(this, ConfiguracoesActivity::class.java))
        }

        aplicarConfiguracoes()
    }

    override fun onResume() {
        super.onResume()
        aplicarConfiguracoes()
    }

    private fun aplicarConfiguracoes() {
        val config = AppConfig.carregar(this)

        // Tradução dos botões
        btnIniciar.text = AppConfig.traduz("Iniciar Exploração", "Start Exploration", config.idioma)
        btnSobre.text = AppConfig.traduz("Sobre o App", "About the App", config.idioma)
        btnConfig.text = AppConfig.traduz("Configurações", "Settings", config.idioma)

        // Tradução do texto descritivo
        txtDescricao.text = AppConfig.traduz(
            "Tecnologia educacional com realidade aumentada",
            "Educational technology with augmented reality",
            config.idioma
        )

        // Tamanho de fonte
        val tamanho = config.tamanhoFonte.toFloat()
        btnIniciar.textSize = tamanho
        btnSobre.textSize = tamanho
        btnConfig.textSize = tamanho
        txtDescricao.textSize = tamanho
    }
}
