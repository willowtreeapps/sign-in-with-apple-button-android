package com.willowtreeapps.signinwithapplebutton.view

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.willowtreeapps.signinwithapplebutton.R

internal enum class SignInColorStyle(@ColorRes val textColor: Int, @DrawableRes val icon: Int,
                            @DrawableRes val background: Int) {
    WHITE(R.color.sign_in_with_apple_button_text_black, R.drawable.sign_in_with_apple_button_icon_black, R.drawable.sign_in_with_apple_button_background_white),
    BLACK(R.color.sign_in_with_apple_button_text_white, R.drawable.sign_in_with_apple_button_icon_white, R.drawable.sign_in_with_apple_button_background_black),
    WHITE_OUTLINE(R.color.sign_in_with_apple_button_text_black, R.drawable.sign_in_with_apple_button_icon_black, R.drawable.sign_in_with_apple_button_background_white_outline)
}