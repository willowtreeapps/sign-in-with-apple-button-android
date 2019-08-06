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

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url

        return when {
            url == null -> {
                false
            }
            url.toString().contains("appleid.apple.com") -> {
                view?.loadUrl(url.toString())
                true
            }
            url.toString().contains(attempt.redirectUri) -> {
                Log.d("SIGN_IN_WITH_APPLE", "Web view was forwarded to redirect URI")

                val codeParameter = url.getQueryParameter("code")
                val stateParameter = url.getQueryParameter("state")

                when {
                    codeParameter == null -> {
                        callback.onSignInFailure(IllegalArgumentException("code not returned"))
                    }
                    stateParameter != attempt.state -> {
                        callback.onSignInFailure(IllegalArgumentException("state does not match"))
                    }
                    else -> {
                        callback.onSignInSuccess(codeParameter)
                    }
                }

                true
            }
            else -> {
                false
            }
        }
    }

}
