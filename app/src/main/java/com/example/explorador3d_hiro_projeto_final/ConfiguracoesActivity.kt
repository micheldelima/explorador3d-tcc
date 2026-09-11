package com.example.explorador3d_hiro_projeto_final

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.explorador3d_hiro_projeto_final.utils.AppConfig
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var radioPortugues: RadioButton
    private lateinit var radioIngles: RadioButton
    private lateinit var radioGroupIdioma: RadioGroup
    private lateinit var switchSom: SwitchMaterial
    private lateinit var seekFonte: SeekBar
    private lateinit var btnSalvar: MaterialButton
    private lateinit var txtFonteLabel: MaterialTextView
    private lateinit var txtTitulo: MaterialTextView
    private lateinit var txtSubTitulo: MaterialTextView
    private lateinit var txtIdiomaLabel: MaterialTextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        try {
            // Inicializa componentes
            txtTitulo = findViewById(R.id.txtTitulo)
            txtSubTitulo = findViewById(R.id.txtSubTitulo)
            txtIdiomaLabel = findViewById(R.id.txtIdiomaLabel)
            radioGroupIdioma = findViewById(R.id.radioGroupIdioma)
            radioPortugues = findViewById(R.id.rbPortugues)
            radioIngles = findViewById(R.id.rbIngles)
            switchSom = findViewById(R.id.switchSom)
            seekFonte = findViewById(R.id.seekFonte)
            txtFonteLabel = findViewById(R.id.txtFonteLabel)
            btnSalvar = findViewById(R.id.btnSalvar)

            prefs = getSharedPreferences("config_app", MODE_PRIVATE)

            // Carrega ou define padrões
            carregarPreferencias()

            // Aplica o idioma e tamanho salvos
            aplicarConfiguracoes()

            // Atualiza dinamicamente o texto da fonte
            seekFonte.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val valor = progress.coerceAtLeast(12)
                    val idioma = AppConfig.carregar(this@ConfiguracoesActivity).idioma
                    txtFonteLabel.text =
                        "${AppConfig.traduz("Tamanho da fonte", "Font size", idioma)}: $valor"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Salvar
            btnSalvar.setOnClickListener { salvarPreferencias() }

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao inicializar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun aplicarConfiguracoes() {
        val config = AppConfig.carregar(this)

        // Traduções
        txtSubTitulo.text = AppConfig.traduz("Configurações do Aplicativo", "App Settings", config.idioma)
        txtIdiomaLabel.text = AppConfig.traduz("Idioma do aplicativo:", "App language:", config.idioma)
        radioPortugues.text = AppConfig.traduz("Português", "Portuguese", config.idioma)
        radioIngles.text = AppConfig.traduz("Inglês", "English", config.idioma)
        switchSom.text = AppConfig.traduz("Ativar som e narração (TTS)", "Enable sound and narration (TTS)", config.idioma)
        txtFonteLabel.text = "${AppConfig.traduz("Tamanho da fonte", "Font size", config.idioma)}: ${seekFonte.progress}"
        btnSalvar.text = AppConfig.traduz("Salvar configurações", "Save settings", config.idioma)

        // Tamanhos
        val tamanho = config.tamanhoFonte.toFloat()
        txtTitulo.textSize = tamanho + 8f
        txtSubTitulo.textSize = tamanho + 2f
        txtIdiomaLabel.textSize = tamanho
        radioPortugues.textSize = tamanho
        radioIngles.textSize = tamanho
        switchSom.textSize = tamanho
        txtFonteLabel.textSize = tamanho
        btnSalvar.textSize = tamanho
    }

    private fun carregarPreferencias() {
        val primeiraVez = !prefs.contains("idioma")

        if (primeiraVez) {
            prefs.edit()
                .putString("idioma", "pt")
                .putBoolean("som", true)
                .putInt("tamanho_fonte", 20)
                .apply()
        }

        val idioma = prefs.getString("idioma", "pt") ?: "pt"
        val somAtivo = prefs.getBoolean("som", true)
        val tamanhoFonte = prefs.getInt("tamanho_fonte", 20).coerceIn(12, 40)

        radioPortugues.isChecked = idioma == "pt"
        radioIngles.isChecked = idioma == "en"
        switchSom.isChecked = somAtivo
        seekFonte.progress = tamanhoFonte
        txtFonteLabel.text = "Tamanho da fonte: $tamanhoFonte"
    }

    private fun salvarPreferencias() {
        try {
            val idiomaSelecionado = if (radioPortugues.isChecked) "pt" else "en"
            val somAtivo = switchSom.isChecked
            val tamanhoFonte = seekFonte.progress.coerceIn(12, 40)

            prefs.edit()
                .putString("idioma", idiomaSelecionado)
                .putBoolean("som", somAtivo)
                .putInt("tamanho_fonte", tamanhoFonte)
                .apply()

            val msg = AppConfig.traduz("Configurações salvas com sucesso!", "Settings saved successfully!", idiomaSelecionado)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao salvar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
