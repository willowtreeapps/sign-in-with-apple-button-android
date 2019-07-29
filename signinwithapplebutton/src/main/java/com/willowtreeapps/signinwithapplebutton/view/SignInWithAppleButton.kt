package com.willowtreeapps.signinwithapplebutton.view

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.R
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Button(context, attrs, defStyleAttr, defStyleRes) {
    var redirectUri: String = ""
    var clientId: String = ""
    var state: String = UUID.randomUUID().toString()
    //TODO: Figure out the behavior/default for scope; default was "email name"
    var scope: String = ""

    var callback: AppleSignInCallback? = null

    var tabPackage: String? = null

    private val connection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
            client?.warmup(0L)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    fun bindService(context: Context) {
        CustomTabsClient.bindCustomTabsService(context, CustomTabsClient.getPackageName(context, null), connection)
    }

    fun unbindService(context: Context) {
        context.unbindService(connection)
    }

    init {
        gravity = Gravity.CENTER
        compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.icon_padding)

        val padding = resources.getDimensionPixelOffset(R.dimen.button_padding_default)

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SignInWithAppleButton, 0, 0)

        val buttonText = attributes.getInt(R.styleable.SignInWithAppleButton_buttonTextType, SignInText.SIGN_IN.ordinal)
        text = resources.getString(SignInText.values()[buttonText].text)

        val buttonColorStyleIndex =
            attributes.getInt(R.styleable.SignInWithAppleButton_buttonColorStyle, SignInTheme.BLACK.ordinal)
        val buttonColorStyle = SignInTheme.values()[buttonColorStyleIndex]
        val radius = attributes.getDimension(
            R.styleable.SignInWithAppleButton_cornerRadius,
            resources.getDimension(R.dimen.corner_radius_default)
        )

        setTextColor(ContextCompat.getColorStateList(context, buttonColorStyle.textColor))

        background = ContextCompat.getDrawable(context, buttonColorStyle.background)
        (background as GradientDrawable).cornerRadius = radius

        val icon = ContextCompat.getDrawable(context, buttonColorStyle.icon)?.mutate()
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

        setPaddingRelative(
            padding - compoundDrawablePadding, padding - compoundDrawablePadding,
            padding, padding - compoundDrawablePadding
        )

        clientId = attributes.getString(R.styleable.SignInWithAppleButton_clientId) ?: clientId
        redirectUri = attributes.getString(R.styleable.SignInWithAppleButton_redirectUri) ?: redirectUri
        state = attributes.getString(R.styleable.SignInWithAppleButton_state) ?: state
        scope = attributes.getString(R.styleable.SignInWithAppleButton_scope) ?: scope

        setOnClickListener {
            buildCustomTabIntent().launchUrl(context, buildUri())
        }

        attributes.recycle()
    }

    /*
    We have to build this URI out ourselves because the default behavior is to POST the response, while we
    need a GET so that we can retrieve the code (and potentially ID token/state). The URI created is based off
    the URI constructed by Apple's Javascript SDK, and is why certain fields (like the version, v) are included
    in the URI construction.

    See the Sign In With Apple Javascript SDK for reference:
    https://developer.apple.com/documentation/signinwithapplejs/configuring_your_webpage_for_sign_in_with_apple
    */
    private fun buildUri() = Uri
        .parse("https://appleid.apple.com/auth/authorize")
        .buildUpon().apply {
            appendQueryParameter("response_type", "code")
            appendQueryParameter("v", "1.1.6")
            appendQueryParameter("redirect_uri", redirectUri)
            appendQueryParameter("client_id", clientId)
            appendQueryParameter("scope", scope)
            appendQueryParameter("state", state)
        }.build()

    private fun buildCustomTabIntent(): CustomTabsIntent {
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(context, R.color.black))
            setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.white))
        }.build()
        customTabsIntent.intent.`package` = tabPackage
        return customTabsIntent
    }
}