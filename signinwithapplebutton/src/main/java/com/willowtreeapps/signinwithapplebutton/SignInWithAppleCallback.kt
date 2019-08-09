package com.willowtreeapps.signinwithapplebutton

interface SignInWithAppleCallback {

    fun onSignInWithAppleSuccess(authorizationCode: String)

    fun onSignInWithAppleFailure(error: Throwable)

    fun onSignInWithAppleCancel()
}

internal fun SignInWithAppleCallback.toFunction(): (SignInWithAppleResult) -> Unit =
    { result ->
        when (result) {
            is SignInWithAppleResult.Success -> onSignInWithAppleSuccess(result.authorizationCode)
            is SignInWithAppleResult.Failure -> onSignInWithAppleFailure(result.error)
            is SignInWithAppleResult.Cancel -> onSignInWithAppleCancel()
        }
    }
