package com.willowtreeapps.signinwithapplebutton.view

import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService

class SignInWebViewClient(
    private val attempt: SignInWithAppleService.AuthenticationAttempt,
    private val callback: SignInWithAppleService.Callback
) : WebViewClient() {

    // for API levels < 24
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return isUrlOverridden(view, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return isUrlOverridden(view, request?.url)
    }

    private fun isUrlOverridden(view: WebView?, url: Uri?): Boolean {
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
