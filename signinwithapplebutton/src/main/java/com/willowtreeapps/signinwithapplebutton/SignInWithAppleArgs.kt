package com.willowtreeapps.signinwithapplebutton

data class SignInWithAppleArgs(
    val clientId: String,
    val redirectUri: String,
    val scope: String
) {

    class Builder {
        private lateinit var clientId: String
        private lateinit var redirectUri: String
        private lateinit var scope: String

        fun clientId(clientId: String) = apply {
            this.clientId = clientId
        }

        fun redirectUri(redirectUri: String) = apply {
            this.redirectUri = redirectUri
        }

        fun scope(scope: String) = apply {
            this.scope = scope
        }

        fun build() = SignInWithAppleArgs(clientId, redirectUri, scope)
    }
}
