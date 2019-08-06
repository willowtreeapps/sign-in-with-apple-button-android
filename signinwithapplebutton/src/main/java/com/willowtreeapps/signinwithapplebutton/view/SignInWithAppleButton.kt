package com.willowtreeapps.signinwithapplebutton.view

import android.R.style.Theme_Black_NoTitleBar_Fullscreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var service: SignInWithAppleService? = null
    private var dialog: Dialog? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.sign_in_with_apple_button, this, true)
    }

    private val textView: TextView = findViewById(R.id.textView)

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

        val colorStyleIndex =
            attributes.getInt(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_colorStyle, SignInColorStyle.BLACK.ordinal)
        val colorStyle = SignInColorStyle.values()[colorStyleIndex]

        val text = attributes.getInt(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_textType, SignInTextType.SIGN_IN.ordinal)

        val cornerRadius = attributes.getDimension(R.styleable.SignInWithAppleButton_sign_in_with_apple_button_cornerRadius, resources.getDimension(R.dimen.sign_in_with_apple_button_cornerRadius_default))

        attributes.recycle()

        background = ContextCompat.getDrawable(context, colorStyle.background)?.mutate()
        (background as GradientDrawable).cornerRadius = cornerRadius

        val iconVerticalOffset = resources.getDimensionPixelOffset(R.dimen.sign_in_with_apple_button_textView_icon_verticalOffset)
        textView.text = resources.getString(SignInTextType.values()[text].text)
        textView.setTextColor(ContextCompat.getColorStateList(context, colorStyle.textColor))

        val icon = ContextCompat.getDrawable(context, colorStyle.icon)?.mutate()

        if (icon != null) {
            icon.setBounds(0, iconVerticalOffset, icon.intrinsicWidth, icon.intrinsicHeight + iconVerticalOffset)

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        setOnClickListener {
            onClick()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        service = null
        dialog = null
    }

    private fun onClick() {
        val service = service
        if (service == null) {
            Log.w("SIGN_IN_WITH_APPLE", "No service configured")
            return
        }

        val authenticationAttempt = service.buildAuthenticationAttempt()
        val webView = buildWebView(authenticationAttempt, service.callback)

        dialog = Dialog(context, Theme_Black_NoTitleBar_Fullscreen)
        dialog?.setContentView(webView)
        webView.loadUrl(authenticationAttempt.authenticationUri)
        dialog?.show()
    }

    private fun buildWebView(
        authenticationAttempt: SignInWithAppleService.AuthenticationAttempt,
        callback: SignInWithAppleService.Callback
    ) = WebView(context).apply {
        val client = SignInWebViewClient(
            authenticationAttempt,
            object : SignInWithAppleService.Callback {
                override fun onSignInSuccess(authorizationCode: String) {
                    callback.onSignInSuccess(authorizationCode)
                    dialog?.dismiss()
                }

                override fun onSignInFailure(error: Throwable) {
                    callback.onSignInFailure(error)
                    dialog?.dismiss()
                }
            }
        )

        webViewClient = client
        settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
    }

}