package com.willowtreeapps.signinwithapplebutton.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
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
    private val icon: Drawable?

    init {
        val attributes =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SignInWithAppleButton,
                0,
                R.style.SignInWithAppleButton
            )

        // Style
        val background =
            attributes.getDrawable(R.styleable.SignInWithAppleButton_android_background)
        val icon = attributes.getDrawable(R.styleable.SignInWithAppleButton_android_drawableLeft)
        val textColor =
            attributes.getColorStateList(R.styleable.SignInWithAppleButton_android_textColor)

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

        this.background = background?.mutate()
        (background as? GradientDrawable)?.cornerRadius = cornerRadius

        if (icon != null) {
            this.icon = DrawableCompat.wrap(icon)

            val iconVerticalOffset =
                resources.getDimensionPixelOffset(R.dimen.sign_in_with_apple_button_textView_icon_verticalOffset)

            this.icon.setBounds(
                0,
                iconVerticalOffset,
                this.icon.intrinsicWidth,
                this.icon.intrinsicHeight + iconVerticalOffset
            )

            textView.setCompoundDrawablesRelative(this.icon, null, null, null)
        } else {
            this.icon = null
        }

        if (textColor != null) {
            setButtonTextColor(textColor)
        }

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

    fun setButtonTextColor(@ColorInt color: Int) {
        textView.setTextColor(color)
        if (Build.VERSION.SDK_INT >= 23) {
            textView.compoundDrawableTintList = ColorStateList.valueOf(color)
        } else if (icon != null) {
            DrawableCompat.setTint(icon.mutate(), color)
        }
    }

    fun setButtonTextColor(color: ColorStateList) {
        textView.setTextColor(color)
        if (Build.VERSION.SDK_INT >= 23) {
            textView.compoundDrawableTintList = color
        } else if (icon != null) {
            DrawableCompat.setTintList(icon.mutate(), color)
        }
    }

    @RequiresApi(21)
    fun setButtonBackgroundColor(@ColorInt color: Int) {
        (background as? GradientDrawable)?.color = ColorStateList.valueOf(color)
    }
}
