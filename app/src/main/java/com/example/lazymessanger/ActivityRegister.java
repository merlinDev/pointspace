package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import controlers.UserManager;

public class ActivityRegister extends AppCompatActivity {

    private static final String TAG = "ActivityRegister";

    EditText tf_name;
    EditText tf_email;
    EditText tf_password;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tf_name = findViewById(R.id.name);
        tf_email = findViewById(R.id.email);
        tf_password = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            redirectToHome();
        }

    }

    public void redirectLogin(View view) {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void registerUser(View view) {

        final String name = tf_name.getText().toString();
        final String email = tf_email.getText().toString();
        final String password = tf_password.getText().toString();

        new UserManager()
                .addUser(this, email, password, name);
    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
