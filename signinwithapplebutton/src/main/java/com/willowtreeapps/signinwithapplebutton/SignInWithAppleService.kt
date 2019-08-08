package com.willowtreeapps.signinwithapplebutton

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.FragmentManager
import com.willowtreeapps.signinwithapplebutton.view.SignInWebViewDialogFragment
import java.util.*

private const val fragmentTag: String = "SignInWithAppleButton"

class SignInWithAppleService(
    private val fragmentManager: FragmentManager,
    private val config: SignInWithAppleConfig,
    private val callback: (SignInWithAppleResult) -> Unit
) {

    constructor(
        fragmentManager: FragmentManager,
        config: SignInWithAppleConfig,
        callback: SignInWithAppleCallback
    ) : this(fragmentManager, config, callback.toFunction())

    init {
        val fragmentIfCreated =
            fragmentManager.findFragmentByTag(fragmentTag) as? SignInWebViewDialogFragment
        fragmentIfCreated?.configure(callback)
    }

    internal data class AuthenticationAttempt(
        val authenticationUri: String,
        val redirectUri: String,
        val state: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "invalid",
            parcel.readString() ?: "invalid",
            parcel.readString() ?: "invalid"
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(authenticationUri)
            parcel.writeString(redirectUri)
            parcel.writeString(state)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AuthenticationAttempt> {

            override fun createFromParcel(parcel: Parcel) = AuthenticationAttempt(parcel)

            override fun newArray(size: Int): Array<AuthenticationAttempt?> = arrayOfNulls(size)

            /*
            The authentication page URI we're creating is based off the URI constructed by Apple's JavaScript SDK,
            which is why certain fields (like the version, v) are included in the URI construction.

            We have to build this URI ourselves because Apple's behavior in JavaScript is to POST the response,
            while we need a GET so we can retrieve the authentication code and verify the state
            merely by intercepting the URL.

            See the Sign In With Apple Javascript SDK for comparison:
            https://developer.apple.com/documentation/signinwithapplejs/configuring_your_webpage_for_sign_in_with_apple
            */
            fun create(
                config: SignInWithAppleConfig,
                state: String = UUID.randomUUID().toString()
            ): AuthenticationAttempt {
                val authenticationUri = Uri
                    .parse("https://appleid.apple.com/auth/authorize")
                    .buildUpon().apply {
                        appendQueryParameter("response_type", "code")
                        appendQueryParameter("v", "1.1.6")
                        appendQueryParameter("client_id", config.clientId)
                        appendQueryParameter("redirect_uri", config.redirectUri)
                        appendQueryParameter("scope", config.scope)
                        appendQueryParameter("state", state)
                    }
                    .build()
                    .toString()

                return AuthenticationAttempt(authenticationUri, config.redirectUri, state)
            }
        }
    }

    fun show() {
        // Only show one instance at a time.
        val currentlyShowing =
            fragmentManager.findFragmentByTag(fragmentTag) as? SignInWebViewDialogFragment
        if (currentlyShowing != null) {
            currentlyShowing.dismiss()
        }
        val fragment = SignInWebViewDialogFragment.newInstance(AuthenticationAttempt.create(config))
        fragment.configure(callback)
        fragment.show(fragmentManager, fragmentTag)
    }

    companion object {
        @JvmStatic
        fun View.setupSignInWithApple(
            fragmentManager: FragmentManager,
            config: SignInWithAppleConfig,
            callback: SignInWithAppleCallback
        ) {
            val service = SignInWithAppleService(fragmentManager, config, callback)
            setOnClickListener { service.show() }
        }
    }
}

fun View.setupSignInWithApple(
    fragmentManager: FragmentManager,
    config: SignInWithAppleConfig,
    callback: (SignInWithAppleResult) -> Unit
) {
    val service = SignInWithAppleService(fragmentManager, config, callback)
    setOnClickListener { service.show() }
}
