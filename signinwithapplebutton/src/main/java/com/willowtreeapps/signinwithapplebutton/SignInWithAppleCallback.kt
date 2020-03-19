package com.willowtreeapps.signinwithapplebutton

interface SignInWithAppleCallback {

    fun onSignInWithAppleSuccess(
        authorizationCode: String,
        idToken: String,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null
    )

    fun onSignInWithAppleFailure(error: Throwable)

    fun onSignInWithAppleCancel()
}

internal fun SignInWithAppleCallback.toFunction(): (SignInWithAppleResult) -> Unit =
    { result ->
        when (result) {
            is SignInWithAppleResult.Success -> onSignInWithAppleSuccess(
                result.authorizationCode, result.idToken, result.email, result.firstName, result.lastName
            )
            is SignInWithAppleResult.Failure -> onSignInWithAppleFailure(result.error)
            is SignInWithAppleResult.Cancel -> onSignInWithAppleCancel()
        }
    }
