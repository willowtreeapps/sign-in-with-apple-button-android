package com.willowtreeapps.signinwithapplebutton.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService

@SuppressLint("SetJavaScriptEnabled")
class SignInWebViewDialogFragment : DialogFragment(), SignInWithAppleCallback {

    private var service: SignInWithAppleService? = null
    private var callback: SignInWithAppleCallback? = null

    private var authenticationAttempt: SignInWithAppleService.AuthenticationAttempt? = null

    private val webViewIfCreated: WebView?
        get() = view as? WebView

    fun configure(service: SignInWithAppleService, callback: SignInWithAppleCallback) {
        this.service = service
        this.callback = callback
    }

    fun beginAuthenticationAttempt() {
        val service = service
        if (service == null) {
            Log.w("SIGN_IN_WITH_APPLE", "Service is not configured")
            return
        }

        this.authenticationAttempt = service.buildAuthenticationAttempt()
    }

    private fun endAuthenticationAttempt() {
        this.authenticationAttempt = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (authenticationAttempt == null) {
            authenticationAttempt = savedInstanceState?.getParcelable("authenticationAttempt")
        }

        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val webView = WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
            }
        }

        if (authenticationAttempt == null) {
            Log.w("SIGN_IN_WITH_APPLE", "Authentication attempt is not configured")
        }

        webView.webViewClient = authenticationAttempt?.let {
            SignInWebViewClient(it,this@SignInWebViewDialogFragment)
        }

        if (savedInstanceState != null) {
            savedInstanceState.getBundle("webView")?.run {
                webView.restoreState(this)
            }
        } else {
            authenticationAttempt?.run {
                webView.loadUrl(authenticationUri)
            }
        }

        return webView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("authenticationAttempt", authenticationAttempt)

        outState.putBundle(
            "webView",
            Bundle().apply {
                webViewIfCreated?.saveState(this)
            }
        )
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    // SignInWithAppleCallback

    override fun onSignInWithAppleSuccess(authorizationCode: String) {
        endAuthenticationAttempt()

        dialog?.dismiss()

        callback?.onSignInWithAppleSuccess(authorizationCode)
    }

    override fun onSignInWithAppleFailure(error: Throwable) {
        endAuthenticationAttempt()

        dialog?.dismiss()

        callback?.onSignInWithAppleFailure(error)
    }

}