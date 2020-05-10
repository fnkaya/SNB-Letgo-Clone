package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if ( firebaseAuth.getCurrentUser() != null)
            startMainActivity();
        else
            startLoginActivity();
        this.finish();
    }

    private void startLoginActivity() {
        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
    }

    private void startMainActivity(){ ;
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
    }
}
