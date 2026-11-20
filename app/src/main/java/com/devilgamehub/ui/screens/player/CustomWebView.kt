package com.devilgamehub.ui.screens.player

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomWebView(
    url: String,
    isMuted: Boolean = false,
    fastLoadingEnabled: Boolean = true,
    onProgressChanged: (Int) -> Unit,
    onErrorReceived: (String) -> Unit,
    modifier: Modifier = Modifier,
    onWebViewCreated: (WebView) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Lifecycle Observer for WebView Pause/Resume and cleanup
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    webViewRef?.onPause()
                    webViewRef?.pauseTimers()
                }
                Lifecycle.Event.ON_RESUME -> {
                    webViewRef?.onResume()
                    webViewRef?.resumeTimers()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webViewRef?.let { webView ->
                webView.stopLoading()
                webView.loadUrl("about:blank")
                webView.onPause()
                webView.removeAllViews()
                webView.destroy()
            }
            webViewRef = null
        }
    }

    // Inject JS to control audio muting inside HTML5 game
    LaunchedEffect(isMuted, webViewRef) {
        webViewRef?.let { webView ->
            val muteJs = """
                (function() {
                    var isMuted = $isMuted;
                    document.querySelectorAll('audio, video').forEach(function(m) {
                        m.muted = isMuted;
                        if (isMuted) { m.pause(); }
                    });
                    if (window.AudioContext || window.webkitAudioContext) {
                        var ctx = window.AudioContext || window.webkitAudioContext;
                        if (ctx && ctx.suspend && ctx.resume) {
                            if (isMuted) ctx.suspend(); else ctx.resume();
                        }
                    }
                })();
            """.trimIndent()
            webView.evaluateJavascript(muteJs, null)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Hardware Acceleration for HTML5 Canvas / 60 FPS rendering
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mediaPlaybackRequiresUserGesture = false
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    
                    cacheMode = if (fastLoadingEnabled) {
                        WebSettings.LOAD_CACHE_ELSE_NETWORK
                    } else {
                        WebSettings.LOAD_DEFAULT
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onProgressChanged(10)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onProgressChanged(100)

                        // Apply Mute JS on page finish
                        val muteJs = """
                            (function() {
                                var isMuted = $isMuted;
                                document.querySelectorAll('audio, video').forEach(function(m) {
                                    m.muted = isMuted;
                                    if (isMuted) { m.pause(); }
                                });
                            })();
                        """.trimIndent()
                        view?.evaluateJavascript(muteJs, null)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            val errorMsg = error?.description?.toString() ?: "Failed to load game"
                            onErrorReceived(errorMsg)
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val reqUrl = request?.url?.toString() ?: return false

                        if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://") || reqUrl.startsWith("file://") || reqUrl.startsWith("about:")) {
                            return false
                        }

                        return try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl))
                            context.startActivity(intent)
                            true
                        } catch (_: Exception) {
                            true
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onProgressChanged(newProgress)
                    }
                }

                webViewRef = this
                onWebViewCreated(this)
                if (url.isNotEmpty()) {
                    loadUrl(url)
                }
            }
        },
        update = { webView ->
            if (webView.url != url && url.isNotEmpty()) {
                webView.loadUrl(url)
            }
        }
    )
}
