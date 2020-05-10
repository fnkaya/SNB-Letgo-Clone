package com.example.sellnbuy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sellnbuy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private final int GOOGLE_SIGN_IN = 1001;

    private TextInputLayout txtLoginEmailLayout;
    private EditText edtLoginEmail;
    private TextInputLayout txtLoginPasswordLayout;
    private EditText edtLoginPassword;
    private SignInButton btnGoogleSignIn;
    private GoogleSignInClient gsic;
    private Button btnLogin;
    private TextView txtForgetPassword;
    private TextView txtLoginToRegister;
    private ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsic = GoogleSignIn.getClient(this, gso);

        init();
        listenActions();
    }



    private void init() {
        txtLoginEmailLayout = findViewById(R.id.text_input_login_email);
        edtLoginEmail = findViewById(R.id.editText_login_email);
        txtLoginPasswordLayout = findViewById(R.id.text_input_login_password);
        edtLoginPassword = findViewById(R.id.editText_login_password);
        btnLogin = findViewById(R.id.button_login);
        btnGoogleSignIn = findViewById(R.id.button_google_signin);
        txtForgetPassword = findViewById(R.id.text_forget_password);
        txtLoginToRegister = findViewById(R.id.text_login_to_register);
        progressBarLogin = findViewById(R.id.progressBar_login);
        progressBarLogin.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
    }



    private void listenActions(){
        txtLoginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtLoginEmail.getText().toString();
                String password = edtLoginPassword.getText().toString();

                if ( validateEmail() && validatePassword() ){
                    progressBarLogin.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLogin.setVisibility(View.VISIBLE);
                startActivityForResult(gsic.getSignInIntent(), GOOGLE_SIGN_IN);
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtLoginEmail.getText().toString();
                showResetPasswordDialog(email);
            }
        });
    }



    private boolean validateEmail(){
        String emailInput = txtLoginEmailLayout.getEditText().getText().toString().trim();

        if ( TextUtils.isEmpty(emailInput) ){
            txtLoginEmailLayout.setError(getString(R.string.field_cant_be_empty));
            return  false;
        }
        else {
            txtLoginEmailLayout.setError(null);
            return true;
        }
    }



    private boolean validatePassword(){
        String passwordInput = txtLoginPasswordLayout.getEditText().getText().toString().trim();

        if ( TextUtils.isEmpty(passwordInput) ){
            txtLoginPasswordLayout.setError(getString(R.string.field_cant_be_empty));
            return  false;
        }
        else {
            txtLoginPasswordLayout.setError(null);
            return true;
        }
    }



    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ){
                            startMainActivity();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBarLogin.setVisibility(View.INVISIBLE);
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == GOOGLE_SIGN_IN ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if ( account != null )
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: ", e);
                progressBarLogin.setVisibility(View.INVISIBLE);
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            progressBarLogin.setVisibility(View.INVISIBLE);
                            startMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.sigin_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void showResetPasswordDialog(String email){
        View view = getLayoutInflater().inflate(R.layout.dialog_reset_password, null, false);
        final EditText edtResetPasswordEmail = view.findViewById(R.id.editText_reset_password_email);
        edtResetPasswordEmail.setText(email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_your_password)
                .setIcon(R.drawable.ic_key)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = edtResetPasswordEmail.getText().toString();
                        if ( !TextUtils.isEmpty(email) )
                            sendResetPasswordEmail(email);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void sendResetPasswordEmail(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if ( task.isSuccessful() )
                            Toast.makeText(LoginActivity.this, R.string.password_reset_link_send, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void startMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
