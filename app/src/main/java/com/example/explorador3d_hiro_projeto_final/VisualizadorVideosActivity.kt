package com.example.explorador3d_hiro_projeto_final

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader
import com.example.explorador3d_hiro_projeto_final.api.LibreTranslateRepository
import java.util.*

class VisualizadorVideosActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var webView: WebView
    private lateinit var tts: TextToSpeech
    private val CAMERA_PERMISSION_REQUEST_CODE = 300

    private val videos = listOf(
        "videos/ar_intro.mp4" to "Introduction to Augmented Reality"
    )

    private var videoAtual = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizador_videos)

        tts = TextToSpeech(this, this)

        // Solicita permissão da câmera
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
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webViewVideo)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request?.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Traduz o título do primeiro vídeo automaticamente
                val nomeInicial = videos[videoAtual].second
                LibreTranslateRepository.traduzir(nomeInicial) { traducao ->
                    val safeTraducao = traducao.replace("'", "\\'")
                    val js = """
                        (function() {
                            if (window.updateVideoInfo) {
                                window.updateVideoInfo('$nomeInicial', '$safeTraducao');
                            }
                            var nomeVideo = document.querySelector('#nomeVideo');
                            if (nomeVideo) {
                                nomeVideo.setAttribute('value', '$nomeInicial');
                            }
                        })();
                    """.trimIndent()
                    runOnUiThread { webView.evaluateJavascript(js, null) }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread { request.grant(request.resources) }
            }
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.mediaPlaybackRequiresUserGesture = false

        webView.loadUrl("https://appassets.androidplatform.net/assets/videos.html")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        tts.stop()
    }

    override fun onDestroy() {
        webView.destroy()
        tts.shutdown()
        super.onDestroy()
    }
}
