package com.willowtreeapps.signinwithapplebutton

sealed class SignInWithAppleResult {
    data class Success(val authorizationCode: String, val idToken: String) : SignInWithAppleResult()

    data class Failure(val error: Throwable) : SignInWithAppleResult()

    object Cancel : SignInWithAppleResult()
}
