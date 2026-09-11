package com.example.explorador3d_hiro_projeto_final

import com.example.explorador3d_hiro_projeto_final.utils.AppConfig
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ExploracaoActivity : AppCompatActivity() {

    private lateinit var btnModelos: MaterialButton
    private lateinit var btnImagens: MaterialButton
    private lateinit var btnVideos: MaterialButton
    private lateinit var txtRodape: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploracao)

        // Inicializa componentes
        btnModelos = findViewById(R.id.btnModelos)
        btnImagens = findViewById(R.id.btnImagens)
        btnVideos = findViewById(R.id.btnVideos)
        txtRodape = findViewById(R.id.txtRodape)

        // --- Abre Modelos 3D ---
        btnModelos.setOnClickListener {
            startActivity(Intent(this, VisualizadorActivity::class.java))
        }

        // --- Abre Imagens AR ---
        btnImagens.setOnClickListener {
            startActivity(Intent(this, ImagensActivity::class.java))
        }

        // --- Abre Vídeos AR ---
        btnVideos.setOnClickListener {
            startActivity(Intent(this, VisualizadorVideosActivity::class.java))
        }

        // Aplica configurações ao abrir
        aplicarConfiguracoes()
    }

    override fun onResume() {
        super.onResume()
        aplicarConfiguracoes() // Reaplica ao voltar da tela de configurações
    }

    private fun aplicarConfiguracoes() {
        val config = AppConfig.carregar(this)

        // Tradução dos botões
        btnModelos.text = AppConfig.traduz("Modelos 3D", "3D Models", config.idioma)
        btnImagens.text = AppConfig.traduz("Imagens", "AR Images", config.idioma)
        btnVideos.text = AppConfig.traduz("Vídeos", "AR Videos", config.idioma)

        // Tradução do rodapé
        txtRodape.text = AppConfig.traduz(
            "Escolha o conteúdo",
            "Choose the content",
            config.idioma
        )

        // Ajuste de tamanho da fonte
        val tamanho = config.tamanhoFonte.toFloat()
        btnModelos.textSize = tamanho
        btnImagens.textSize = tamanho
        btnVideos.textSize = tamanho
        txtRodape.textSize = tamanho
    }
}
