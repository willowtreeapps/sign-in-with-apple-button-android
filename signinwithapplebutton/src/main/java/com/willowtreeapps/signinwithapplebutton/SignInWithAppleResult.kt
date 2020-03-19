package com.willowtreeapps.signinwithapplebutton

sealed class SignInWithAppleResult {
    data class Success(
        val authorizationCode: String,
        val idToken: String,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
    ) : SignInWithAppleResult()

    data class Failure(val error: Throwable) : SignInWithAppleResult()

    object Cancel : SignInWithAppleResult()
}
