package com.willowtreeapps.signinwithapplebutton.view

import androidx.annotation.StringRes
import com.willowtreeapps.signinwithapplebutton.R

enum class SignInText(@StringRes val text: Int) {
    SIGN_IN(R.string.sign_in_with_apple),
    CONTINUE(R.string.continue_with_apple)
}