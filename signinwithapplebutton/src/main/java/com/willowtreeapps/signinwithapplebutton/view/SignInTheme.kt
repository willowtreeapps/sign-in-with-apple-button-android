package com.willowtreeapps.signinwithapplebutton.view

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.willowtreeapps.signinwithapplebutton.R

enum class SignInTheme(@ColorRes val textColor: Int, @DrawableRes val icon: Int,
                       @DrawableRes val background: Int) {
    WHITE(R.color.black_text, R.drawable.ic_apple_logo_black, R.drawable.white),
    BLACK(R.color.white_text, R.drawable.ic_apple_logo_white, R.drawable.black),
    WHITE_WITH_OUTLINE(R.color.black_text, R.drawable.ic_apple_logo_black, R.drawable.white_with_outline)
}