package com.willowtreeapps.signinwithapple.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback;
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService;
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton;

import static android.widget.Toast.LENGTH_SHORT;

public class SampleJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        SignInWithAppleButton signInWithAppleButtonBlack = findViewById(R.id.sign_in_with_apple_button_black);
        SignInWithAppleButton signInWithAppleButtonWhite = findViewById(R.id.sign_in_with_apple_button_white);
        SignInWithAppleButton signInWithAppleButtonWhiteOutline = findViewById(R.id.sign_in_with_apple_button_white_outline);

        // Replace clientId and redirectUri with your own values.
        SignInWithAppleService service = new SignInWithAppleService(
                "com.your.client.id.here",
                "https://your-redirect-uri.com/callback",
                "email name"
        );

        signInWithAppleButtonBlack.configure(getSupportFragmentManager(), service, callback);
        signInWithAppleButtonWhite.configure(getSupportFragmentManager(), service, callback);
        signInWithAppleButtonWhiteOutline.configure(getSupportFragmentManager(), service, callback);
    }

    private final SignInWithAppleCallback callback = new SignInWithAppleCallback() {
        @Override
        public void onSignInWithAppleSuccess(@NonNull String authorizationCode) {
            Toast.makeText(SampleJavaActivity.this, authorizationCode, LENGTH_SHORT).show();
        }

        @Override
        public void onSignInWithAppleFailure(@NonNull Throwable error) {
            Log.d("SAMPLE_APP", "Received error from Apple Sign In " + error.getMessage());
        }

        @Override
        public void onSignInWithAppleCancel() {
            Log.d("SAMPLE_APP", "User canceled Apple Sign In");
        }
    };
}
