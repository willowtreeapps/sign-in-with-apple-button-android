package com.willowtreeapps.signinwithapplebutton.view

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess
import java.lang.IllegalArgumentException
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Button(context, attrs, defStyleAttr, defStyleRes) {
    lateinit var redirectUri: String
    lateinit var clientId: String
    var state: String = UUID.randomUUID().toString()
    //TODO: Figure out the behavior/default for scope; default was "email name"
    var scope: String = ""

    var callback: AppleSignInCallback? = null

    private val webClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return when {
                request?.url?.toString()?.contains("appleid.apple.com") == true -> {
                    view?.loadUrl(request.url?.toString() ?: "")
                    true
                }
                request?.url?.toString()?.contains(redirectUri) == true -> {
                    Log.d("APPLE_REDIRECT", "${request.url?.toString()}")
                    val code = request.url.getQueryParameter("code")
                    val idToken = request.url.getQueryParameter("id_token")
                    val newState = request.url.getQueryParameter("state")
                    when {
                        code == null -> callback?.onSignInFailure(IllegalArgumentException("no code returned"))
                        idToken == null -> callback?.onSignInFailure(IllegalArgumentException("no id token returned"))
                        state != newState -> callback?.onSignInFailure(IllegalArgumentException("states do not match"))
                        else -> callback?.onSignInSuccess(AppleSignInSuccess(code, idToken))
                    }
                    true
                }
                else -> false
            }
        }
    }

    init {
        text = resources.getString(R.string.sign_in_with_apple)
        setOnClickListener {
            val webView = WebView(getContext())
            webView.webViewClient = webClient
            webView.settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
            }
            /*
            We have to build this URI out ourselves because the default behavior is to POST the response, while we
            need a GET so that we can retrieve the code (and potentially ID token/state). The URI created is based off
            the URI constructed by Apple's Javascript SDK, and is why certain fields (like the version, v) are included
            in the URI construction.

            See the Sign In With Apple Javascript SDK for reference:
            https://developer.apple.com/documentation/signinwithapplejs/configuring_your_webpage_for_sign_in_with_apple
             */
            val uri = Uri.parse("https://appleid.apple.com/auth/authorize")
                .buildUpon().apply {
                    appendQueryParameter("response_type", "code id_token")
                    appendQueryParameter("v", "1.1.6")
                    appendQueryParameter("redirect_uri", redirectUri)
                    appendQueryParameter("client_id", clientId)
                    appendQueryParameter("scope", scope)
                    appendQueryParameter("state", state)
                }.build().toString()

            val dialog = Dialog(context, Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(webView)
            webView.loadUrl(uri)
            dialog.show()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        callback = null
    }
}