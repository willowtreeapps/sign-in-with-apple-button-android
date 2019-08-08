package com.willowtreeapps.signinwithapple.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleClient
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SampleActivity : AppCompatActivity(), SignInWithAppleClient {
    private lateinit var signInWithAppleButtonBlack: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhite: SignInWithAppleButton
    private lateinit var signInWithAppleButtonWhiteOutline: SignInWithAppleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        signInWithAppleButtonBlack = findViewById(R.id.sign_in_with_apple_button_black)
        signInWithAppleButtonWhite = findViewById(R.id.sign_in_with_apple_button_white)
        signInWithAppleButtonWhiteOutline = findViewById(R.id.sign_in_with_apple_button_white_outline)
    }

    override fun onStart() {
        super.onStart()

        // Replace clientId and redirectUri with your own values.
        val service = SignInWithAppleService(
            clientId = "com.your.client.id.here",
            redirectUri = "https://your-redirect-uri.com/callback",
            scope = "email name"
        )

        val client: SignInWithAppleClient = this

        signInWithAppleButtonBlack.configure(service, client)
        signInWithAppleButtonWhite.configure(service, client)
        signInWithAppleButtonWhiteOutline.configure(service, client)
    }

    // SignInWithAppleClient

    override fun getFragmentManagerForSignInWithApple(): FragmentManager {
        return supportFragmentManager
    }

    override fun onSignInWithAppleSuccess(authorizationCode: String) {
        Toast.makeText(this, authorizationCode, LENGTH_SHORT).show()
    }

    override fun onSignInWithAppleFailure(error: Throwable) {
        Log.d("SAMPLE_APP", "Received error from Apple Sign In ${error.message}")
    }

    override fun onSignInWithAppleCancel() {
        Log.d("SAMPLE_APP", "User canceled Apple Sign In")
    }
}
