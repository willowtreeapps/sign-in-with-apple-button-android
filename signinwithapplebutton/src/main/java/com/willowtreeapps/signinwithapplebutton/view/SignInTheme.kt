package com.willowtreeapps.signinwithapplebutton.view

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.willowtreeapps.signinwithapplebutton.R

//TODO: Get Apple logo for drawable
enum class SignInTheme(@ColorRes val textColor: Int, @DrawableRes val icon: Int,
                       @DrawableRes val background: Int) {
    WHITE(R.color.black, R.drawable.ic_apple_logo, R.drawable.white),
    BLACK(R.color.white, R.drawable.ic_apple_logo, R.drawable.black),
    WHITE_WITH_OUTLINE(R.color.black, R.drawable.ic_apple_logo, R.drawable.white_with_outline)
}