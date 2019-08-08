package com.willowtreeapps.signinwithapplebutton

import androidx.fragment.app.FragmentManager

interface SignInWithAppleClient : SignInWithAppleCallback {
    fun getFragmentManagerForSignInWithApple(): FragmentManager
}
