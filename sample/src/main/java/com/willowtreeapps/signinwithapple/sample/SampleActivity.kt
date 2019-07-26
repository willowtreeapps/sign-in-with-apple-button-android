package com.willowtreeapps.signinwithapple.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.willowtreeapps.signinwithapplebutton.AppleSignInCallback
import com.willowtreeapps.signinwithapplebutton.model.AppleSignInSuccess
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SampleActivity : AppCompatActivity() {
    lateinit var signInWithAppleButtonBlack: SignInWithAppleButton
    lateinit var signInWithAppleButtonWhite: SignInWithAppleButton
    lateinit var signInWithAppleButtonWhiteOutline: SignInWithAppleButton

    private val signInCallback = object : AppleSignInCallback {
        override fun onSignInSuccess(signInSuccess: AppleSignInSuccess) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSignInFailure(error: Throwable) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        signInWithAppleButtonBlack.callback = signInCallback
    }
}
