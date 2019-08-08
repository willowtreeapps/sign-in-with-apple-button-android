package com.willowtreeapps.signinwithapplebutton.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.willowtreeapps.signinwithapplebutton.R
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleClient
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import java.util.*

class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private class SavedState : BaseSavedState {

        var fragmentTag: String? = null

        constructor(source: Parcel) : super(source) {
            fragmentTag = source.readString()
        }

        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeString(fragmentTag)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

    private var service: SignInWithAppleService? = null
    private var client: SignInWithAppleClient? = null
    private var fragmentTag: String? = null
        set(value) {
            field = value
            configureFragmentIfCreated()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.sign_in_with_apple_button, this, true)
    }

    private val textView: TextView = findViewById(R.id.textView)

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

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
            icon.setBounds(0, iconVerticalOffset, icon.intrinsicWidth, icon.intrinsicHeight + iconVerticalOffset)

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        setOnClickListener {
            onClick()
        }
    }

    fun configure(service: SignInWithAppleService, client: SignInWithAppleClient) {
        this.service = service
        this.client = client

        configureFragmentIfCreated()
    }

    private fun configureFragmentIfCreated() {
        val service = service
        val client = client

        if (service == null || client == null) {
            return
        }

        val fragmentIfCreated = fragmentTag?.let {
            client.getFragmentManagerForSignInWithApple().findFragmentByTag(fragmentTag)
                    as? SignInWebViewDialogFragment
        }

        fragmentIfCreated?.configure(client)
    }

    private fun onClick() {
        val service = service
        if (service == null) {
            Log.w("SIGN_IN_WITH_APPLE", "Service is not configured")
            return
        }

        val client = client
        if (client == null) {
            Log.w("SIGN_IN_WITH_APPLE", "Client is not configured")
            return
        }

        fragmentTag = UUID.randomUUID().toString()

        val fragment = SignInWebViewDialogFragment(service.buildAuthenticationAttempt())
        fragment.configure(client)

        fragment.show(client.getFragmentManagerForSignInWithApple(), fragmentTag)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val savedState = super.onSaveInstanceState()?.let {
            SavedState(it)
        }

        savedState?.fragmentTag = fragmentTag
        return savedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            fragmentTag = state.fragmentTag
        } else {
            super.onRestoreInstanceState(state)
        }
    }

}