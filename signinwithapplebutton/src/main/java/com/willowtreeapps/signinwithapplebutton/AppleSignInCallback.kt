package com.willowtreeapps.signinwithapplebutton

import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess

interface AppleSignInCallback {
    fun onSignInSuccess(signInSuccess: AppleSignInSuccess)

    fun onSignInFailure(error: Throwable)
}