package com.willowtreeapps.signinwithapplebutton.view

import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton.Companion.SIGN_IN_WITH_APPLE_LOG_TAG
import org.json.JSONObject

internal class SignInWebViewClient(
    private val attempt: SignInWithAppleService.AuthenticationAttempt,
    private val callback: (SignInWithAppleResult) -> Unit
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
                Log.d(SIGN_IN_WITH_APPLE_LOG_TAG, "Web view was forwarded to redirect URI")

                val codeParameter = url.getQueryParameter("code")
                val idTokenParameter = url.getQueryParameter("id_token")
                val stateParameter = url.getQueryParameter("state")

                when {
                    codeParameter == null || idTokenParameter == null -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("code or idToken not returned")))
                    }
                    stateParameter != attempt.state -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("state does not match")))
                    }
                    else -> {
                        val (email, firstName, lastName) = parseUser(url.getQueryParameter("user"))
                        callback(SignInWithAppleResult.Success(codeParameter, idTokenParameter, email, firstName, lastName))
                    }
                }

                true
            }
            else -> {
                false
            }
        }
    }

    private fun parseUser(userParameter: String?): Triple<String?, String?, String?> {
        if (userParameter.isNullOrEmpty()) {
            return Triple(null, null, null)
        }
        val userObject = try {
            JSONObject(userParameter)
        } catch (e: Exception) {
            return Triple(null, null, null)
        }
        val email: String? = if (userObject.has("email")) {
            userObject.getString("email")
        } else {
            null
        }
        val nameObject = userObject.optJSONObject("name")
        val firstName: String? = try {
            if (nameObject?.has("firstName") == true) {
                nameObject.getString("firstName")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
        val lastName: String? = try {
            if (nameObject?.has("lastName") == true) {
                nameObject.getString("lastName")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
        return Triple(email, firstName, lastName)
    }
}
