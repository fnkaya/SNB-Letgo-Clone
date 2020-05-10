package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sellnbuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputLayout txtRegisterNameLayout;
    private TextInputEditText edtRegisterName;
    private TextInputLayout txtRegisterEmailLayout;
    private TextInputEditText edtRegisterEmail;
    private TextInputLayout txtRegisterPasswordLayout;
    private TextInputEditText edtRegisterPassword;
    private TextView txtRegisterToLogin;
    private Button btnRegister;
    private ProgressBar progressBarRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        listenActions();
    }



    private void init() {
        txtRegisterNameLayout = findViewById(R.id.text_input_register_name);
        edtRegisterName = findViewById(R.id.editText_register_name);
        txtRegisterEmailLayout = findViewById(R.id.text_input_register_email);
        edtRegisterEmail = findViewById(R.id.editText_register_email);
        txtRegisterPasswordLayout = findViewById(R.id.text_input_register_password);
        edtRegisterPassword = findViewById(R.id.editText_register_password);
        txtRegisterToLogin = findViewById(R.id.text_register_to_login);
        btnRegister = findViewById(R.id.button_register);
        progressBarRegister = findViewById(R.id.progressBar_register);
        progressBarRegister.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
    }



    private void listenActions() {
        txtRegisterToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtRegisterName.getText().toString();
                String email = edtRegisterEmail.getText().toString();
                String password = edtRegisterPassword.getText().toString();

                if ( validateName() && validateEmail() && validatePassword() ) {
                    progressBarRegister.setVisibility(View.VISIBLE);
                    registerUser(name, email, password);
                }
            }
        });
    }



    private boolean validateName(){
        String nameInput = txtRegisterNameLayout.getEditText().getText().toString().trim();

        if( TextUtils.isEmpty(nameInput) ){
            txtRegisterNameLayout.setError(getString(R.string.field_cant_be_empty));
            return false;
        }
        else if ( nameInput.length() > 30 ){
            txtRegisterNameLayout.setError(getString(R.string.name_too_long));
            return false;
        }
        else{
            txtRegisterNameLayout.setError(null);
            return true;
        }
    }



    private boolean validateEmail(){
        String emailInput = txtRegisterEmailLayout.getEditText().getText().toString().trim();

        if ( TextUtils.isEmpty(emailInput) ){
            txtRegisterEmailLayout.setError(getString(R.string.field_cant_be_empty));
            return  false;
        }
        else {
            txtRegisterEmailLayout.setError(null);
            return true;
        }
    }



    private boolean validatePassword(){
        String passwordInput = txtRegisterPasswordLayout.getEditText().getText().toString().trim();

        if ( TextUtils.isEmpty(passwordInput) ){
            txtRegisterPasswordLayout.setError(getString(R.string.field_cant_be_empty));
            return  false;
        }
        else if ( passwordInput.length() < 6 ){
            txtRegisterPasswordLayout.setError(getString(R.string.password_must_be_6));
            return  false;
        }
        else {
            txtRegisterPasswordLayout.setError(null);
            return true;
        }
    }



    private void registerUser(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserInformation(name);
                        } else {
                            Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBarRegister.setVisibility(View.INVISIBLE);
                    }
                });
    }



    private void saveUserInformation(String name) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(request)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startMainActivity();
                            } else {
                                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private void startMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
