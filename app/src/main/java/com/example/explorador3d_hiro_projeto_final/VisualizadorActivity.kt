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

class VisualizadorActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var webView: WebView
    private var tts: TextToSpeech? = null
    private var somAtivo: Boolean = true
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private val modelos = listOf(
        "modelos/aviao.glb" to "Airplane",
        "modelos/livro.glb" to "Book",
        "modelos/Boat.glb" to "Boat",
        "modelos/PlanetEarthandtheMoon.glb" to "Planet Earth",
        "modelos/Helicoptero.glb" to "Helicopter",
        "modelos/Dinossauro.glb" to "Dinosaur",
        "modelos/Castle.glb" to "Castle",
        "modelos/carro.glb" to "Car",
        "modelos/jogo_de_xadrez.glb" to "Chess Game",
        "modelos/cachorro.glb" to "Dog",
        "modelos/peixe.glb" to "Fish",
        "modelos/submarino.glb" to "Submarine",
        "modelos/leao.glb" to "Lion",
        "modelos/motocicleta.glb" to "Motorcycle",
        "modelos/sistema_solar.glb" to "Solar System"

    )

    private val escalas = listOf(
        "0.005 0.005 0.005",  // airplane
        "0.03 0.03 0.03", // books
        "0.018 0.018 0.018",  // Boat
        "0.11 0.11 0.11",  // Planet Earth
        "0.001 0.001 0.001", // Helicopter
        "0.07 0.07 0.07",  // Dinosaur
        "0.03 0.03 0.03",  // Castle
        "0.1 0.1 0.1", // carro
        "10 10 10", // chess
        "0.3 0.3 0.3", // dog
        "0.003 0.003 0.003", // fish
        "0.06 0.06 0.06", // submarine
        "0.1 0.1 0.1", // lion
        "0.01 0.01 0.01", // motorcycle
        "0.7 0.7 0.7"  // solar system
    )

    private var modeloAtual = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizador)

        // Carrega configurações globais
        val config = AppConfig.carregar(this)
        somAtivo = config.somAtivo

        // Inicializa TTS apenas se o som estiver ativo
        if (somAtivo) {
            tts = TextToSpeech(this, this)
        }

        // Permissão da câmera
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

        val btnOuvir = findViewById<Button>(R.id.btnOuvir)
        val btnProximo = findViewById<Button>(R.id.btnProximo)

        // --- Botão "Ouvir" ---
        btnOuvir.setOnClickListener {
            val nome = modelos[modeloAtual].second
            if (somAtivo && tts != null) {
                tts?.speak("This is a $nome", TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "Sound is disabled in settings.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Botão "Próximo" ---
        btnProximo.setOnClickListener {
            modeloAtual = (modeloAtual + 1) % modelos.size
            val novoModelo = modelos[modeloAtual].first
            val novaEscala = escalas[modeloAtual]
            val nomeIngles = modelos[modeloAtual].second

            val jsCode = """
                (function() {
                    var entity = document.querySelector('#modelo3d');
                    if (!entity) {
                        setTimeout(arguments.callee, 500);
                        return;
                    }
                    entity.removeAttribute('gltf-model');
                    entity.setAttribute('gltf-model', '$novoModelo');
                    entity.setAttribute('scale', '$novaEscala');
                    console.log('Modelo trocado para $novoModelo ($nomeIngles)');
                })();
            """.trimIndent()

            webView.evaluateJavascript(jsCode, null)
            Toast.makeText(this, "Model: $nomeIngles", Toast.LENGTH_SHORT).show()

            // Atualiza texto e tradução exibida no painel AR
            LibreTranslateRepository.traduzir(nomeIngles) { traducao ->
                val safeTraducao = traducao.replace("'", "\\'")
                val js = """
                    (function() {
                        if (window.updateModelInfo) {
                            window.updateModelInfo('$nomeIngles', '$safeTraducao');
                        }
                        var nome3d = document.querySelector('#nome3d');
                        if (nome3d) {
                            nome3d.setAttribute('value', '$nomeIngles');
                        }
                    })();
                """.trimIndent()
                runOnUiThread { webView.evaluateJavascript(js, null) }
            }
        }
    }

    // Permissões da câmera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupWebView()
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // WebView (AR.js)
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webViewAR)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request?.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.postDelayed({
                    val nomeInicial = modelos[modeloAtual].second
                    LibreTranslateRepository.traduzir(nomeInicial) { traducao ->
                        val safeTraducao = traducao.replace("'", "\\'")
                        val js = """
                            (function() {
                                if (window.updateModelInfo) {
                                    window.updateModelInfo('$nomeInicial', '$safeTraducao');
                                }
                                var nome3d = document.querySelector('#nome3d');
                                if (nome3d) {
                                    nome3d.setAttribute('value', '$nomeInicial');
                                }
                            })();
                        """.trimIndent()
                        runOnUiThread { webView.evaluateJavascript(js, null) }
                    }
                }, 1000)
            }
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

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

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
