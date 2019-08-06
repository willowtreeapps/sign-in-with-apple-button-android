package com.willowtreeapps.signinwithapplebutton.view

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService

class SignInWebViewClient(
    private val attempt: SignInWithAppleService.AuthenticationAttempt,
    private val callback: SignInWithAppleService.Callback
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = when {
        request?.url?.toString()?.contains("appleid.apple.com") == true -> {
            view?.loadUrl(request.url?.toString() ?: "")
            true
        }
        request?.url?.toString()?.contains(attempt.redirectUri) == true -> {
            Log.d("SIGN_IN_WITH_APPLE", "${request.url?.toString()}")

            val codeParameter = request.url.getQueryParameter("code")
            val stateParameter = request.url.getQueryParameter("state")

            when {
                codeParameter == null -> {
                    callback.onSignInFailure(IllegalArgumentException("code not returned"))
                }
                attempt.state != stateParameter -> {
                    callback.onSignInFailure(IllegalArgumentException("state does not match"))
                }
                else -> {
                    callback.onSignInSuccess(codeParameter)
                }
            }

            true
        }
        else -> false
    }

}
