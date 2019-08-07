package com.willowtreeapps.signinwithapplebutton

interface SignInWithAppleCallback {
    fun onSignInWithAppleSuccess(authorizationCode: String)
    fun onSignInWithAppleFailure(error: Throwable)
}
