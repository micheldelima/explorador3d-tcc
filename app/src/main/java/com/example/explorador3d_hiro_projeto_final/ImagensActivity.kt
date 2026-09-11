package com.example.explorador3d_hiro_projeto_final

import com.example.explorador3d_hiro_projeto_final.api.LibreTranslateRepository
import com.example.explorador3d_hiro_projeto_final.utils.AppConfig
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader
import java.util.*

class ImagensActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var webView: WebView
    private var tts: TextToSpeech? = null
    private var somAtivo: Boolean = true
    private var idiomaApp: String = "pt"
    private val CAMERA_PERMISSION_REQUEST_CODE = 200

    private val imagens = listOf(
        "imagens/boy_reading.png" to "The boy is reading a book.",
        "imagens/girl_running.png" to "The girl is running.",
        "imagens/cat_sleeping.png" to "The cat is sleeping.",
        "imagens/dog_playing.png" to "The dog is playing with a ball.",
        "imagens/sun_shining.avif" to "The sun is shining."
    )

    private var imagemAtual = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagens)

        // Carrega as configurações salvas
        val config = AppConfig.carregar(this)
        somAtivo = config.somAtivo
        idiomaApp = config.idioma

        // Inicializa TTS apenas se o som estiver ativado
        if (somAtivo) {
            tts = TextToSpeech(this, this)
        }

        // Pede permissão da câmera e carrega WebView
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            setupWebView()
        }

        val btnOuvir = findViewById<Button>(R.id.btnOuvirImagem)
        val btnProxima = findViewById<Button>(R.id.btnProximaImagem)

        // --- Botão "Ouvir" ---
        btnOuvir.setOnClickListener {
            val frase = imagens[imagemAtual].second

            if (somAtivo && tts != null) {
                // Fala o texto em inglês
                tts?.speak(frase, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                // Apenas mostra aviso se o som estiver desativado
                val msg = if (idiomaApp == "pt")
                    "Som desativado nas configurações."
                else
                    "Sound is disabled in settings."
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // --- Botão "Próxima imagem" ---
        btnProxima.setOnClickListener {
            imagemAtual = (imagemAtual + 1) % imagens.size
            val novaImagem = imagens[imagemAtual].first
            val fraseIngles = imagens[imagemAtual].second

            // Atualiza imagem e texto no HTML
            val jsCode = """
                (function() {
                    var img = document.querySelector('#imagemAR');
                    if (!img) {
                        setTimeout(arguments.callee, 500);
                        return;
                    }
                    img.setAttribute('src', '$novaImagem');
                    var nome = document.querySelector('#nomeImagem');
                    if (nome) nome.setAttribute('value', '$fraseIngles');
                })();
            """.trimIndent()

            webView.evaluateJavascript(jsCode, null)
            Toast.makeText(this, "Imagem: $fraseIngles", Toast.LENGTH_SHORT).show()

            // Tradução automática exibida abaixo
            LibreTranslateRepository.traduzir(fraseIngles) { traducao ->
                val safeTraducao = traducao.replace("'", "\\'")
                val js = """
                    (function() {
                        if (window.updateImageInfo) {
                            window.updateImageInfo('$fraseIngles', '$safeTraducao');
                        }
                    })();
                """.trimIndent()
                runOnUiThread {
                    webView.evaluateJavascript(js, null)
                }
            }
        }
    }

    // Permissões
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupWebView()
            } else {
                Toast.makeText(this, "Permissão da câmera é necessária.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Configuração da WebView
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webViewARImagens)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?) =
                assetLoader.shouldInterceptRequest(request?.url)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread { request.grant(request.resources) }
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            allowUniversalAccessFromFileURLs = true
            allowFileAccessFromFileURLs = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.loadUrl("https://appassets.androidplatform.net/assets/imagens.html")
    }

    // Inicializa idioma do TTS
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        tts?.stop()
    }

    override fun onDestroy() {
        webView.destroy()
        tts?.shutdown()
        super.onDestroy()
    }
}
