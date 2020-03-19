package com.willowtreeapps.signinwithapplebutton.view

import android.annotation.TargetApi
import android.os.Build
import android.os.Build.VERSION_CODES.N
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService
import com.willowtreeapps.signinwithapplebutton.toFunction
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SignInWebViewClientTest {

    private lateinit var attempt: SignInWithAppleService.AuthenticationAttempt
    private lateinit var callback: (SignInWithAppleResult) -> Unit
    private lateinit var client: SignInWebViewClient

    @Before
    fun tearUp() {
        attempt = mockkClass(SignInWithAppleService.AuthenticationAttempt::class) {
            every { redirectUri } returns "https://test.com/redirect"
            every { state } returns "state"
        }
        callback = mockkClass(SignInWithAppleCallback::class) {
            every { onSignInWithAppleSuccess(any(), any(), any(), any(), any()) } just runs
            every { onSignInWithAppleFailure(any()) } just runs
        }.toFunction()
        client = SignInWebViewClient(attempt, callback)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun success_result_parsed_with_user_successfully() {
        val overrided = client.shouldOverrideUrlLoading(
            null,
            "https://test.com/redirect?state=state&code=code&id_token=id_token&user=%7B%22name%22%3A%7B%22firstName%22%3A%22Test%22%2C%22lastName%22%3A%22User%22%7D%2C%22email%22%3A%22test%40test.com%22%7D"
        )
        assertEquals(true, overrided)
        verify {
            callback(
                SignInWithAppleResult.Success(
                    "code", "id_token", "test@test.com", "Test", "User"
                )
            )
        }
    }

    @Test
    fun success_result_parsed_with_no_user_successfully() {
        val overrided = client.shouldOverrideUrlLoading(
            null,
            "https://test.com/redirect?state=state&code=code&id_token=id_token"
        )
        assertEquals(true, overrided)
        verify {
            callback(
                SignInWithAppleResult.Success(
                    "code", "id_token", null, null, null
                )
            )
        }
    }

    @Test
    fun success_result_parsed_with_invalid_user_successfully() {
        val overrided = client.shouldOverrideUrlLoading(
            null,
            "https://test.com/redirect?state=state&code=code&id_token=id_token&user=asdf"
        )
        assertEquals(true, overrided)
        verify {
            callback(
                SignInWithAppleResult.Success(
                    "code", "id_token", null, null, null
                )
            )
        }
    }

    @Test
    fun success_result_parsed_with_invalid_user_name_and_valid_email_successfully() {
        val overrided = client.shouldOverrideUrlLoading(
            null,
            "https://test.com/redirect?state=state&code=code&id_token=id_token&user=%7B%22name%22%3A+%22asdf%22%2C+%22email%22%3A+%22test@test.com%22%7D"
        )
        assertEquals(true, overrided)
        verify {
            callback(
                SignInWithAppleResult.Success(
                    "code", "id_token", "test@test.com", null, null
                )
            )
        }
    }

    @Test
    fun null_url_will_not_be_overrided() {
        assertEquals(
            false,
            client.shouldOverrideUrlLoading(null, mockkClass(WebResourceRequest::class) {
                every { url } returns null
            })
        )
    }

    @Test
    fun url_contains_apple_id_url_will_be_loaded_manually() {
        val appleIdUrl = "https://appleid.apple.com"
        val webView = mockkClass(WebView::class) {
            every { loadUrl(any()) } just runs
        }
        val overrided = client.shouldOverrideUrlLoading(webView, appleIdUrl)

        assertEquals(true, overrided)
        verify {
            webView.loadUrl(appleIdUrl)
        }
    }

    @Test
    fun url_without_apple_id_url_and_redirect_url_will_not_be_overrided() {
        val overrided = client.shouldOverrideUrlLoading(null, "https:/someurl.com")

        assertEquals(false, overrided)
    }

    @Test
    fun url_without_id_token_parameter_failed() {
        val overrided = client.shouldOverrideUrlLoading(null, "https://test.com/redirect?state=state&code=code")

        assertEquals(true, overrided)
        verify { callback(SignInWithAppleResult.Failure(any())) }
    }

    @Test
    fun url_without_code_parameter_failed() {
        val overrided = client.shouldOverrideUrlLoading(null, "https://test.com/redirect?state=state&id_token=id_token")

        assertEquals(true, overrided)
        verify { callback(SignInWithAppleResult.Failure(any())) }
    }

    @Test
    fun url_with_wrong_state_parameter_failed() {
        val overrided = client.shouldOverrideUrlLoading(null, "https://test.com/redirect?state=wrong_state")

        assertEquals(true, overrided)
        verify { callback(SignInWithAppleResult.Failure(any())) }
    }
}