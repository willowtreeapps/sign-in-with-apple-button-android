package com.willowtreeapps.signinwithapple.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SampleActivity : AppCompatActivity() {
    private lateinit var signInWithAppleButtonBlack: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhite: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhiteOutline: SignInWithAppleButton

    private val signInCallback = object : AppleSignInCallback {
        override fun onSignInSuccess(signInSuccess: AppleSignInSuccess) {
            Toast.makeText(this@SampleActivity, signInSuccess.code, LENGTH_SHORT).show()
        }

        override fun onSignInFailure(error: Throwable) {
            Log.d("APPLE_SIGN_IN", "Received error from Apple Sign In ${error.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        signInWithAppleButtonBlack = findViewById(R.id.sign_in_with_apple_button_black)
        signInWithAppleButtonWhite = findViewById(R.id.sign_in_with_apple_button_white)
        signInWithAppleButtonWhiteOutline = findViewById(R.id.sign_in_with_apple_button_white_outline)

        signInWithAppleButtonBlack.bindService(this)
        signInWithAppleButtonWhite.bindService(this)
        signInWithAppleButtonWhiteOutline.bindService(this)
    }

    override fun onResume() {
        super.onResume()
        setUpSignInButton(signInWithAppleButtonBlack)
        setUpSignInButton(signInWithAppleButtonWhite)
        setUpSignInButton(signInWithAppleButtonWhiteOutline)
    }

    override fun onStop() {
        super.onStop()
        signInWithAppleButtonBlack.callback = null
        signInWithAppleButtonWhite.callback = null
        signInWithAppleButtonWhiteOutline.callback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        signInWithAppleButtonBlack.unbindService(this)
        signInWithAppleButtonWhite.unbindService(this)
        signInWithAppleButtonWhiteOutline.unbindService(this)
    }

    private fun setUpSignInButton(button: SignInWithAppleButton) = button.apply {
        //TODO: Replace redirectUri and clientId with your own values
        redirectUri = "https://kconner.com/sign-in-with-apple-button-android-example-app/callback"
        clientId = "com.kevinconner.sign-in-with-apple-button-android-example-site"
        scope = "email name"
//        redirectUri = "com.your.client.id.here"
//        clientId = "https://your-redirect-uri.com/callback"
//        scope = "email name"
        callback = signInCallback
    }
}
