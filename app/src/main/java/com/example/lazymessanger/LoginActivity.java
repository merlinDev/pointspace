package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import controlers.UserManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth;

    EditText tf_email;
    EditText tf_password;

    TextInputLayout l_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tf_email = findViewById(R.id.email);
        tf_password = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();

        l_email = findViewById(R.id.l_email);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            redirectToHome();
        }

    }

    public void redirectRegister(View view) {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {
        final String email = tf_email.getText().toString();
        final String password = tf_password.getText().toString();


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {

                    Log.d(TAG, "onComplete: user logged in.");
                    redirectToHome();
                    UserManager userManager = new UserManager();
                    userManager.registerMessagingToken(auth);
                }

            } else {
                task.getException().printStackTrace();
                Toast.makeText(LoginActivity.this, "something is wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
