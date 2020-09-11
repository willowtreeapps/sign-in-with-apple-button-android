package com.willowtreeapps.signinwithapplebutton.view

import android.accounts.NetworkErrorException
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.constants.Strings
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton.Companion.SIGN_IN_WITH_APPLE_LOG_TAG

/**
 * Will do some of the work here:
 * - Use shouldOverrideUrlLoading for checking redirect URL & current URL by using .startsWith()
 */
internal class SignInWebViewClient(
    private val attempt: SignInWithAppleService.AuthenticationAttempt,
    private val callback: (SignInWithAppleResult) -> Unit
) : WebViewClient() {

    private val TAG: String = ::SignInWebViewClient.javaClass.simpleName
    private var success: Boolean = false
    private var failed: Boolean = false
    private var gotError: Boolean = false

    // for API levels < 24
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return isUrlOverridden(view, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return isUrlOverridden(view, request?.url)
    }

    // TODO: Optimizations
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
        Log.d(TAG, "STEP 2: onPageStarted : $url")
        checkStatusFromURL(view, url)
        // processURL(url)
        super.onPageStarted(view, url, favicon)
    }

    // TODO: Optimizations
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPageFinished(view: WebView?, url: String) {
        Log.d(TAG, "STEP 3: onPageFinished : $url")
        // Log.d(TAG, "gotError? $gotError")
        checkStatusFromURL(view, url)
        // processURL(url)
        // finish
        // hideProgress()
        // Log.d(TAG, "SHOULD GO BACK? : " + shouldAllowBack())
        super.onPageFinished(view, url)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, url: String) {
        Log.d(TAG, "onReceivedError: $url")
        gotError = true // set status to avoid override by onPageFinished()
        super.onReceivedError(view, errorCode, description, url)
        callback(SignInWithAppleResult.Failure(NetworkErrorException("onReceivedError")))
    }

    private fun isUrlOverridden(view: WebView?, url: Uri?): Boolean {
        return when {
            url == null -> {
                false
            }
            url.toString().contains(Strings.APPLEID_URL) -> {
                view?.loadUrl(url.toString())
                true
            }
            url.toString().contains(attempt.redirectUri) -> {
                Log.d(SIGN_IN_WITH_APPLE_LOG_TAG, "Web view was forwarded to redirect URI")
                val codeParameter = url.getQueryParameter("code")
                val stateParameter = url.getQueryParameter("state")
                when {
                    codeParameter == null -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("code not returned")))
                    }
                    stateParameter != attempt.state -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("state does not match")))
                    }
                    else -> {
                        callback(SignInWithAppleResult.Success(codeParameter))
                    }
                }
                true
            }
            else -> {
                false
            }
        }
    }

    /*
    private fun processURL(url: String) {
        if (url.startsWith(redirectUrl.getSuccessUrl())) {
            checkoutSuccess = true
        } else if (url.startsWith(redirectUrl.getCancelUrl())) {
            checkoutCancelled = true
        } else if (url.startsWith(redirectUrl.getFailureUrl())) {
            checkoutFailed = true
        }
    }
     */

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun checkStatusFromURL(view: WebView?, url: String) {
        if (!gotError) {
            if (url.contains(attempt.redirectUri)) {
                // Evaluate Javascript
                // This is based on PayMaya WebView document
                val scriptForV1 = "(function() { return (document.getElementsByClassName('link')[0].href); })();"
                // Log.d(TAG, "USING VERSION 2??? $usesVersion2")
                // Evaluate with existing webview
                view?.evaluateJavascript(scriptForV1, ValueCallback<String> { string ->
                    Log.d(TAG, "contains: $string")
                    // val stringFormatted = string.replace("\"".toRegex(), "")
                    /*
                    if (stringFormatted == redirectUrl.getSuccessUrl()) {
                        checkoutSuccess = true
                    } else if (stringFormatted == redirectUrl.getCancelUrl()) {
                        checkoutCancelled = true
                    } else if (stringFormatted == redirectUrl.getFailureUrl()) {
                        checkoutFailed = true
                    }
                     */
                })
                hideProgress()
                // setDisableGoingBack(false);
            }
        } else {
            // error
            failed = true
        }
    }

    /*
    fun onBackPressed() {
        if (success) {
            callback(SignInWithAppleResult.Success("COODEDED"))
        }
        else if (failed) {
            callback(SignInWithAppleResult.Failure("TEST"))
        }
    }
     */

    private fun hideProgress() {
        // val progress: ProgressBar findViewById(R.id.a)
        // progress.setVisibility(View.GONE)
        Log.e(TAG, "NO PROGRESS BAR YET!")
    }

}
