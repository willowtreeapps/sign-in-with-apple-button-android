package com.willowtreeapps.signinwithapple.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SampleActivity : AppCompatActivity() {

    private lateinit var signInWithAppleButtonBlack: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhite: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhiteOutline: SignInWithAppleButton

    private val signInCallback = object : SignInWithAppleService.Callback {
        override fun onSignInSuccess(authorizationCode: String) {
            Toast.makeText(this@SampleActivity, authorizationCode, LENGTH_SHORT).show()
        }

        override fun onSignInFailure(error: Throwable) {
            Log.d("SIGN_IN_WITH_APPLE", "Received error from Apple Sign In ${error.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        signInWithAppleButtonBlack = findViewById(R.id.sign_in_with_apple_button_black)
        signInWithAppleButtonWhite = findViewById(R.id.sign_in_with_apple_button_white)
        signInWithAppleButtonWhiteOutline = findViewById(R.id.sign_in_with_apple_button_white_outline)
    }

    override fun onResume() {
        super.onResume()

        // Replace clientId and redirectUri with your own values.
        val service = SignInWithAppleService(
            clientId = "com.your.client.id.here",
            redirectUri = "https://your-redirect-uri.com/callback",
            scope = "email name",
            callback = signInCallback
        )

        signInWithAppleButtonBlack.service = service
        signInWithAppleButtonWhite.service = service
        signInWithAppleButtonWhiteOutline.service = service
    }

    override fun onStop() {
        super.onStop()

        signInWithAppleButtonBlack.service = null
        signInWithAppleButtonWhite.service = null
        signInWithAppleButtonWhiteOutline.service = null
    }

}
