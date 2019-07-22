package com.willowtreeapps.signinwithapplebutton

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.Button

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Button(context, attrs, defStyleAttr, defStyleRes) {

    init {
        setOnClickListener {
            val webView = WebView(getContext())
            webView.settings.apply {
                javaScriptEnabled = true;
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
}