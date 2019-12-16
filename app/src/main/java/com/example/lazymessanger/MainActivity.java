package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        Intent intent;

        if (auth.getCurrentUser() != null) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, ActivityRegister.class);
        }
        startActivity(intent);
        finish();
    }

}
