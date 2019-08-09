package com.willowtreeapps.signinwithapplebutton.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.willowtreeapps.signinwithapplebutton.*

class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    internal companion object {
        const val SIGN_IN_WITH_APPLE_LOG_TAG = "SIGN_IN_WITH_APPLE"
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.sign_in_with_apple_button, this, true)
    }

    private val textView: TextView = findViewById(R.id.textView)

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, R.style.SignInWithAppleButton)

        // Style
        val background = attributes.getDrawable(R.styleable.SignInWithAppleButton_android_background)
        val icon = attributes.getDrawable(R.styleable.SignInWithAppleButton_android_drawableLeft)
        val textColor = attributes.getColorStateList(R.styleable.SignInWithAppleButton_android_textColor)

        // Text type
        val text = attributes.getInt(
            R.styleable.SignInWithAppleButton_sign_in_with_apple_button_textType,
            SignInTextType.SIGN_IN.ordinal
        )

        // Corner radius
        val cornerRadius = attributes.getDimension(
            R.styleable.SignInWithAppleButton_sign_in_with_apple_button_cornerRadius,
            resources.getDimension(R.dimen.sign_in_with_apple_button_cornerRadius_default)
        )

        attributes.recycle()

        this.background = background?.mutate()?.also {
            updateCornerRadius(it, cornerRadius)
        }

        if (icon != null) {
            val iconVerticalOffset =
                resources.getDimensionPixelOffset(R.dimen.sign_in_with_apple_button_textView_icon_verticalOffset)

            icon.setBounds(
                0,
                iconVerticalOffset,
                icon.intrinsicWidth,
                icon.intrinsicHeight + iconVerticalOffset
            )

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        textView.setTextColor(textColor)
        textView.text = resources.getString(SignInTextType.values()[text].text)
    }

    fun setUpSignInWithAppleOnClick(
        fragmentManager: FragmentManager,
        configuration: SignInWithAppleConfiguration,
        callback: (SignInWithAppleResult) -> Unit
    ) {
        val fragmentTag = "SignInWithAppleButton-$id-SignInWebViewDialogFragment"
        val service = SignInWithAppleService(fragmentManager, fragmentTag, configuration, callback)
        setOnClickListener { service.show() }
    }

    fun setUpSignInWithAppleOnClick(
        fragmentManager: FragmentManager,
        configuration: SignInWithAppleConfiguration,
        callback: SignInWithAppleCallback
    ) {
        setUpSignInWithAppleOnClick(fragmentManager, configuration, callback.toFunction())
    }

    private fun updateCornerRadius(drawable: Drawable, cornerRadius: Float) {
        if (drawable is GradientDrawable) {
            drawable.cornerRadius = cornerRadius
        } else if (drawable is LayerDrawable) {
            drawable.findDrawableByLayerId(android.R.id.background)?.let {
                updateCornerRadius(it, cornerRadius)
            }
        }
    }
}
