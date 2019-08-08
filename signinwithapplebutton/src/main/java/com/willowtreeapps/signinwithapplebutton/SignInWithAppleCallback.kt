package com.willowtreeapps.signinwithapplebutton

interface SignInWithAppleCallback {
    fun onSignInWithAppleSuccess(authorizationCode: String)

    fun onSignInWithAppleCancel()

    fun onSignInWithAppleFailure(error: Throwable)
}
