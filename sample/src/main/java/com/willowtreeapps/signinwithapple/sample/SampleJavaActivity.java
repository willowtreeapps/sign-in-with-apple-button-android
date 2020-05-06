package com.willowtreeapps.signinwithapple.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.willowtreeapps.signinwithapplebutton.SignInWithAppleConfiguration;
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback;
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleService;
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton;

import org.jetbrains.annotations.NotNull;

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
        SignInWithAppleConfiguration configuration = new SignInWithAppleConfiguration.Builder()
                .clientId("com.your.client.id.here")
                .redirectUri("https://your-redirect-uri.com/callback")
                .responseType(SignInWithAppleConfiguration.ResponseType.ALL)
                .scope(SignInWithAppleConfiguration.Scope.ALL)
                .build();

        SignInWithAppleCallback callback = new SignInWithAppleCallback() {
            @Override
            public void onSignInWithAppleSuccess(@NotNull String authorizationCode, @NotNull String idToken) {
                Toast.makeText(SampleJavaActivity.this, "authorizationCode : "+authorizationCode+"   \n   idToken : "+idToken, LENGTH_SHORT).show();
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

        signInWithAppleButtonBlack.setUpSignInWithAppleOnClick(getSupportFragmentManager(), configuration, callback);
        signInWithAppleButtonWhite.setUpSignInWithAppleOnClick(getSupportFragmentManager(), configuration, callback);
        signInWithAppleButtonWhiteOutline.setUpSignInWithAppleOnClick(getSupportFragmentManager(), configuration, callback);
    }
}
