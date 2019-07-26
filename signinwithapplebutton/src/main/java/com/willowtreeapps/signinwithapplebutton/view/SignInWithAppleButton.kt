package com.willowtreeapps.signinwithapplebutton.view

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.core.content.ContextCompat
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Button(context, attrs, defStyleAttr, defStyleRes) {
    var redirectUri: String = ""
    var clientId: String = ""
    var state: String = UUID.randomUUID().toString()
    //TODO: Figure out the behavior/default for scope; default was "email name"
    var scope: String = ""

    var dialog: Dialog? = null

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
                    val newState = request.url.getQueryParameter("state")
                    when {
                        code == null -> callback?.onSignInFailure(IllegalArgumentException("no code returned"))
                        state != newState -> callback?.onSignInFailure(IllegalArgumentException("states do not match"))
                        else -> callback?.onSignInSuccess(AppleSignInSuccess(code))
                    }
                    dialog?.dismiss()
                    true
                }
                else -> false
            }
        }
    }

    init {
        gravity = Gravity.CENTER
        compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.icon_padding)

        val padding = resources.getDimensionPixelOffset(R.dimen.button_padding_default)

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

        val buttonText = attributes.getInt(R.styleable.SignInWithAppleButton_buttonTextType, SignInText.SIGN_IN.ordinal)
        text = resources.getString(SignInText.values()[buttonText].text)

        val buttonThemeIndex =
            attributes.getInt(R.styleable.SignInWithAppleButton_buttonTheme, SignInTheme.BLACK.ordinal)
        val buttonTheme = SignInTheme.values()[buttonThemeIndex]
        val radius = attributes.getDimension(
            R.styleable.SignInWithAppleButton_cornerRadius,
            resources.getDimension(R.dimen.corner_radius_default)
        )

        setTextColor(ContextCompat.getColorStateList(context, buttonTheme.textColor))

        background = ContextCompat.getDrawable(context, buttonTheme.background)
        (background as GradientDrawable).cornerRadius = radius

        val icon = ContextCompat.getDrawable(context, buttonTheme.icon)?.mutate()
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

        setPaddingRelative(padding - compoundDrawablePadding, padding  - compoundDrawablePadding,
            padding, padding  - compoundDrawablePadding)

        clientId = attributes.getString(R.styleable.SignInWithAppleButton_clientId) ?: clientId
        redirectUri = attributes.getString(R.styleable.SignInWithAppleButton_redirectUri) ?: redirectUri
        state = attributes.getString(R.styleable.SignInWithAppleButton_state) ?: state
        scope = attributes.getString(R.styleable.SignInWithAppleButton_scope) ?: scope

        setOnClickListener {
            val webView = buildWebView()
            dialog = Dialog(context, Theme_Black_NoTitleBar_Fullscreen)
            dialog?.setContentView(webView)
            webView.loadUrl(buildUri())
            dialog?.show()
        }

        attributes.recycle()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        callback = null
        dialog = null
    }

    /*
    We have to build this URI out ourselves because the default behavior is to POST the response, while we
    need a GET so that we can retrieve the code (and potentially ID token/state). The URI created is based off
    the URI constructed by Apple's Javascript SDK, and is why certain fields (like the version, v) are included
    in the URI construction.

    See the Sign In With Apple Javascript SDK for reference:
    https://developer.apple.com/documentation/signinwithapplejs/configuring_your_webpage_for_sign_in_with_apple
    */
    private fun buildUri() = Uri
        .parse("https://appleid.apple.com/auth/authorize")
        .buildUpon().apply {
            appendQueryParameter("response_type", "code")
            appendQueryParameter("v", "1.1.6")
            appendQueryParameter("redirect_uri", redirectUri)
            appendQueryParameter("client_id", clientId)
            appendQueryParameter("scope", scope)
            appendQueryParameter("state", state)
        }.build()
        .toString()

    private fun buildWebView() = WebView(context).apply {
        webViewClient = webClient
        settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
    }
}