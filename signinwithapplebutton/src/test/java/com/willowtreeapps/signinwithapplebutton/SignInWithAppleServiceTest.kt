package com.willowtreeapps.signinwithapplebutton

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SignInWithAppleServiceTest {

    @Test
    fun authentication_attempt_parcels_correctly() {
        val attempt = SignInWithAppleService.AuthenticationAttempt(
            authenticationUri = "https://appleid.apple.com/auth/authorize",
            redirectUri = "https://your-redirect-uri.com/callback",
            state = "123"
        )

        val parcel = Parcel.obtain()
        parcel.writeParcelable(attempt, 0)
        parcel.setDataPosition(0)
        val newAttempt =
            parcel.readParcelable<SignInWithAppleService.AuthenticationAttempt>(javaClass.classLoader)
        parcel.recycle()

        assertEquals(newAttempt, attempt)
    }

    @Test
    fun creates_authentication_attempt_based_on_service_args() {
        val attempt = SignInWithAppleService.AuthenticationAttempt.create(
            SignInWithAppleConfiguration(
                clientId = "com.your.client.id.here",
                redirectUri = "https://your-redirect-uri.com/callback",
                scope = "email"
            ),
            state = "state"
        )

        assertEquals(
            "https://appleid.apple.com/auth/authorize?response_type=code&v=1.1.6&client_id=com.your.client.id.here&redirect_uri=https%3A%2F%2Fyour-redirect-uri.com%2Fcallback&scope=email&state=state",
            attempt.authenticationUri
        )
        assertEquals("https://your-redirect-uri.com/callback", attempt.redirectUri)
        assertEquals("state", attempt.state)
    }
}

