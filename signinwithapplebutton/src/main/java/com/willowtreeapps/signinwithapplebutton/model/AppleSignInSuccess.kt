package com.willowtreeapps.signinwithapplebutton.model

//TODO: Figure out what to do with the idToken field
data class AppleSignInSuccess(val code: String, val idToken: String)