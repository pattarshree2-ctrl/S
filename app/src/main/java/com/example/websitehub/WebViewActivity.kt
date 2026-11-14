package com.example.websitehub

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NAME = "website_name"
        const val EXTRA_URL = "website_url"
    }

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        title = intent.getStringExtra(EXTRA_NAME) ?: "Website"
        progress = findViewById(R.id.pageProgress)
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val scheme = request.url.scheme?.lowercase()
                return scheme != "http" && scheme != "https"
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                progress.visibility = View.VISIBLE
                progress.progress = 0
            }

            override fun onPageFinished(view: WebView, url: String?) {
                progress.visibility = View.GONE
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
        findViewById<Button>(R.id.backButton).setOnClickListener {
            if (webView.canGoBack()) webView.goBack() else finish()
        }
        findViewById<Button>(R.id.forwardButton).setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }
        findViewById<Button>(R.id.refreshButton).setOnClickListener { webView.reload() }
        findViewById<Button>(R.id.homeButton).setOnClickListener { finish() }

        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrBlank() || !url.startsWith("https://") && !url.startsWith("http://")) {
            finish()
            return
        }
        webView.loadUrl(url)
    }

    @Deprecated("Deprecated in Android 13; retained for older devices")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) webView.destroy()
        super.onDestroy()
    }
}
