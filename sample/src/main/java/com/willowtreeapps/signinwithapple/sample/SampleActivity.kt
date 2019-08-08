package com.willowtreeapps.signinwithapple.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val signInWithAppleButtonBlack: SignInWithAppleButton = findViewById(R.id.sign_in_with_apple_button_black)
        val signInWithAppleButtonWhite: SignInWithAppleButton = findViewById(R.id.sign_in_with_apple_button_white)
        val signInWithAppleButtonWhiteOutline: SignInWithAppleButton = findViewById(R.id.sign_in_with_apple_button_white_outline)

        // Replace clientId and redirectUri with your own values.
        val service = SignInWithAppleService(
            clientId = "com.your.client.id.here",
            redirectUri = "https://your-redirect-uri.com/callback",
            scope = "email name"
        )

        signInWithAppleButtonBlack.configure(supportFragmentManager, service, ::callback)
        signInWithAppleButtonWhite.configure(supportFragmentManager, service, ::callback)
        signInWithAppleButtonWhiteOutline.configure(supportFragmentManager, service, ::callback)
    }

    private fun callback(result: SignInWithAppleResult) {
        when (result) {
            is SignInWithAppleResult.Success -> {
                Toast.makeText(this, result.authorizationCode, LENGTH_SHORT).show()
            }
            is SignInWithAppleResult.Failure -> {
                Log.d("SAMPLE_APP", "Received error from Apple Sign In ${result.error.message}")
            }
            is SignInWithAppleResult.Cancel -> {
                Log.d("SAMPLE_APP", "User canceled Apple Sign In")
            }
        }
    }
}
