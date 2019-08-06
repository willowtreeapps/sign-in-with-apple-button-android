package com.willowtreeapps.signinwithapplebutton.view

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    var clientId: String = ""
    var redirectUri: String = ""
    var scope: String = ""

    var state: String = UUID.randomUUID().toString()

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
        LayoutInflater.from(context).inflate(R.layout.sign_in_with_apple_button, this, true)
    }

    private val textView: TextView = findViewById(R.id.textView)

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

        clientId = attributes.getString(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_clientId) ?: clientId
        redirectUri = attributes.getString(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_redirectUri) ?: redirectUri
        state = attributes.getString(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_state) ?: state
        scope = attributes.getString(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_scope) ?: scope

        val colorStyleIndex =
            attributes.getInt(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_colorStyle, SignInTheme.BLACK.ordinal)
        val colorStyle = SignInTheme.values()[colorStyleIndex]

        val text = attributes.getInt(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_textType, SignInText.SIGN_IN.ordinal)

        val cornerRadius = attributes.getDimension(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_cornerRadius, resources.getDimension(R.dimen.sign_in_with_apple_button_cornerRadius_default))

        attributes.recycle()

        background = ContextCompat.getDrawable(context, colorStyle.background)?.mutate()
        (background as GradientDrawable).cornerRadius = cornerRadius

        val iconVerticalOffset = resources.getDimensionPixelOffset(R.dimen.sign_in_with_apple_button_textView_icon_verticalOffset)
        textView.text = resources.getString(SignInText.values()[text].text)
        textView.setTextColor(ContextCompat.getColorStateList(context, colorStyle.textColor))

        val icon = ContextCompat.getDrawable(context, colorStyle.icon)?.mutate()
        if (icon != null) {
            icon.setBounds(0, iconVerticalOffset, icon.intrinsicWidth, icon.intrinsicHeight + iconVerticalOffset)

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        setOnClickListener {
            val webView = buildWebView()
            dialog = Dialog(context, Theme_Black_NoTitleBar_Fullscreen)
            dialog?.setContentView(webView)
            webView.loadUrl(buildUri())
            dialog?.show()
        }
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
            appendQueryParameter("client_id", clientId)
            appendQueryParameter("redirect_uri", redirectUri)
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