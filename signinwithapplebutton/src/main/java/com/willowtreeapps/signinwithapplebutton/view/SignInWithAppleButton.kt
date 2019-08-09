package com.willowtreeapps.signinwithapplebutton.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService

class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    internal companion object {
        const val SIGN_IN_WITH_APPLE_LOG_TAG = "SIGN_IN_WITH_APPLE"
    }

    private var fragmentManager: FragmentManager? = null
    private var service: SignInWithAppleService? = null
    private var callback: ((SignInWithAppleResult) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.sign_in_with_apple_button, this, true)
    }

    private val textView: TextView = findViewById(R.id.textView)

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

        val colorStyleIndex =
            attributes.getInt(
                R.styleable.SignInWithAppleButton_sign_in_with_apple_button_colorStyle,
                SignInColorStyle.BLACK.ordinal
            )
        val colorStyle = SignInColorStyle.values()[colorStyleIndex]

        val text = attributes.getInt(
            R.styleable.SignInWithAppleButton_sign_in_with_apple_button_textType,
            SignInTextType.SIGN_IN.ordinal
        )

        val cornerRadius = attributes.getDimension(
            R.styleable.SignInWithAppleButton_sign_in_with_apple_button_cornerRadius,
            resources.getDimension(R.dimen.sign_in_with_apple_button_cornerRadius_default)
        )

        attributes.recycle()

        background = ContextCompat.getDrawable(context, colorStyle.background)?.mutate()
        (background as GradientDrawable).cornerRadius = cornerRadius

        val iconVerticalOffset =
            resources.getDimensionPixelOffset(R.dimen.sign_in_with_apple_button_textView_icon_verticalOffset)
        textView.text = resources.getString(SignInTextType.values()[text].text)
        textView.setTextColor(ContextCompat.getColorStateList(context, colorStyle.textColor))

        val icon = ContextCompat.getDrawable(context, colorStyle.icon)?.mutate()

        if (icon != null) {
            icon.setBounds(
                0,
                iconVerticalOffset,
                icon.intrinsicWidth,
                icon.intrinsicHeight + iconVerticalOffset
            )

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        setOnClickListener {
            onClick()
        }
    }

    fun configure(
        fragmentManager: FragmentManager,
        service: SignInWithAppleService,
        callback: SignInWithAppleCallback
    ) {
        configure(fragmentManager, service) { result ->
            when (result) {
                is SignInWithAppleResult.Success -> callback.onSignInWithAppleSuccess(result.authorizationCode)
                is SignInWithAppleResult.Failure -> callback.onSignInWithAppleFailure(result.error)
                is SignInWithAppleResult.Cancel -> callback.onSignInWithAppleCancel()
            }
        }
    }

    fun configure(
        fragmentManager: FragmentManager,
        service: SignInWithAppleService,
        callback: (SignInWithAppleResult) -> Unit
    ) {
        this.fragmentManager = fragmentManager
        this.service = service
        this.callback = callback

        val fragmentIfCreated =
            fragmentManager.findFragmentByTag(fragmentTag) as? SignInWebViewDialogFragment
        fragmentIfCreated?.configure(callback)
    }

    private val fragmentTag: String
        get() = "SignInWithAppleButton-$id-SignInWebViewDialogFragment"

    private fun onClick() {
        val service = service
        if (service == null) {
            Log.w(SIGN_IN_WITH_APPLE_LOG_TAG, "Service is not configured")
            return
        }

        val callback = callback
        if (callback == null) {
            Log.w(SIGN_IN_WITH_APPLE_LOG_TAG, "Callback is not configured")
            return
        }

        val fragment = SignInWebViewDialogFragment.newInstance(service.buildAuthenticationAttempt())
        fragment.configure(callback)

        fragment.show(fragmentManager, fragmentTag)
    }

}