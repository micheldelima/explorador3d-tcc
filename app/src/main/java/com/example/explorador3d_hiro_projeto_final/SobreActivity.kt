package com.example.explorador3d_hiro_projeto_final

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.explorador3d_hiro_projeto_final.utils.AppConfig
import com.google.android.material.textview.MaterialTextView

class SobreActivity : AppCompatActivity() {

    private lateinit var txtTitulo: MaterialTextView
    private lateinit var txtDescricao: MaterialTextView
    private lateinit var txtCreditos: MaterialTextView
    private lateinit var txtUniversidade: MaterialTextView
    private lateinit var txtVersao: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)

        // Inicializa componentes do layout
        txtTitulo = findViewById(R.id.txtTitulo)
        txtDescricao = findViewById(R.id.txtDescricao)
        txtCreditos = findViewById(R.id.txtCreditos)
        txtUniversidade = findViewById(R.id.txtUniversidade)
        txtVersao = findViewById(R.id.txtVersao)

        aplicarConfiguracoes()
    }

    override fun onResume() {
        super.onResume()
        aplicarConfiguracoes()
    }

    private fun aplicarConfiguracoes() {
        val config = AppConfig.carregar(this)
        val tamanho = config.tamanhoFonte.toFloat()

        // Traduções
        txtTitulo.text = AppConfig.traduz(
            "Sobre o Aplicativo",
            "About the App",
            config.idioma
        )

        txtDescricao.text = AppConfig.traduz(
            """
            O Explorador 3D é um aplicativo educacional de realidade aumentada desenvolvido
            para tornar o aprendizado do idioma inglês mais interativo e envolvente.
            Ele utiliza marcadores físicos (Hiro) para exibir modelos 3D, imagens e vídeos
            de forma dinâmica, permitindo que os alunos explorem conteúdos de maneira imersiva.
            """.trimIndent(),
            """
            The 3D Explorer is an educational augmented reality app developed
            to make English learning more interactive and engaging.
            It uses physical markers (Hiro) to display 3D models, images, and videos
            dynamically, allowing students to explore content immersively.
            """.trimIndent(),
            config.idioma
        )

        txtCreditos.text = AppConfig.traduz(
            "Desenvolvido por Michel de Lima - 4º ano - Engenharia de Software",
            "Developed by Michel de Lima - 4th Year - Software Engineering",
            config.idioma
        )

        txtUniversidade.text = AppConfig.traduz(
            "Universidade Estadual de Ponta Grossa (UEPG)",
            "State University of Ponta Grossa (UEPG)",
            config.idioma
        )

        txtVersao.text = AppConfig.traduz(
            "Versão 1.1 — 2026",
            "Version 1.1 — 2026",
            config.idioma
        )

        // Tamanho das fontes
        txtTitulo.textSize = tamanho + 6
        txtDescricao.textSize = tamanho
        txtCreditos.textSize = tamanho - 2
        txtUniversidade.textSize = tamanho - 2
        txtVersao.textSize = tamanho - 4
    }
}
