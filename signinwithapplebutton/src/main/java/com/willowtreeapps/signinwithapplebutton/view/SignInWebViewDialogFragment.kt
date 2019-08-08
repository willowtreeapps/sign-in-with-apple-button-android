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
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton.Companion.SIGN_IN_WITH_APPLE_LOG_TAG

@SuppressLint("SetJavaScriptEnabled")
internal class SignInWebViewDialogFragment : DialogFragment, SignInWithAppleCallback {

    private companion object {
        const val AUTHENTICATION_ATTEMPT_KEY = "authenticationAttempt"
        const val WEB_VIEW_KEY = "webView"
    }

    private var authenticationAttempt: SignInWithAppleService.AuthenticationAttempt? = null
    private var callback: SignInWithAppleCallback? = null

    private val webViewIfCreated: WebView?
        get() = view as? WebView

    constructor() : super() {
        authenticationAttempt = null
    }

    constructor(authenticationAttempt: SignInWithAppleService.AuthenticationAttempt) {
        this.authenticationAttempt = authenticationAttempt
    }

    fun configure(
        callback: SignInWithAppleCallback
    ) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (authenticationAttempt == null) {
            authenticationAttempt = savedInstanceState?.getParcelable(AUTHENTICATION_ATTEMPT_KEY)
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
            Log.e(SIGN_IN_WITH_APPLE_LOG_TAG, "Authentication attempt is not configured")
        }

        webView.webViewClient = authenticationAttempt?.let {
            SignInWebViewClient(it,this)
        }

        if (savedInstanceState != null) {
            savedInstanceState.getBundle(WEB_VIEW_KEY)?.run {
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

        outState.putParcelable(AUTHENTICATION_ATTEMPT_KEY, authenticationAttempt)

        outState.putBundle(
            WEB_VIEW_KEY,
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
        dialog?.dismiss()

        val callback = callback
        if (callback == null) {
            Log.e(SIGN_IN_WITH_APPLE_LOG_TAG, "Callback is not configured")
            return
        }

        callback.onSignInWithAppleSuccess(authorizationCode)
    }

    override fun onSignInWithAppleFailure(error: Throwable) {
        dialog?.dismiss()

        val callback = callback
        if (callback == null) {
            Log.e(SIGN_IN_WITH_APPLE_LOG_TAG, "Callback is not configured")
            return
        }

        callback.onSignInWithAppleFailure(error)
    }

}