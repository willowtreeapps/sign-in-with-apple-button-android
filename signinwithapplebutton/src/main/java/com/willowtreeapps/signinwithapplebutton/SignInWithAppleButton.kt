package com.willowtreeapps.signinwithapplebutton

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.core.view.updateLayoutParams

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Button(context, attrs, defStyleAttr, defStyleRes) {

    init {
        text = resources.getString(R.string.sign_in_with_apple)
        setOnClickListener {
            val webView = WebView(getContext())
            webView.webViewClient = AppleWebViewClient()
            webView.settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            val dialog = Dialog(context, Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(webView)
            webView.loadUrl("file:///android_asset/sign-in.html")
            dialog.show()
        }
    }

    class AppleWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) =
            if (request?.url?.toString()?.contains("appleid.apple.com") == true) {
                view?.loadUrl(request.url?.toString() ?: "")
                true
            } else {
                false
            }
    }
}