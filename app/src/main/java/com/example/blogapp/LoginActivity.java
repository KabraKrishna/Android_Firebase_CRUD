package com.example.blogapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity  extends AppCompatActivity {

    private EditText email, password;
    private TextView registerLink;
    private ProgressBar loginProgressBar;
    private Button loginButton;
    private Intent homeActivity;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.btn_login);
        loginProgressBar = findViewById(R.id.login_progress);
        loginProgressBar.setVisibility(View.INVISIBLE);
        registerLink = findViewById(R.id.text_register);

        homeActivity = new Intent(this, Home.class);

        mAuth = FirebaseAuth.getInstance();

        addListners();
    }

    private void addListners() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String loginEmail = email.getText().toString();
                final String loginPasswd = password.getText().toString();

                loginButton.setVisibility(View.INVISIBLE);
                loginProgressBar.setVisibility(View.VISIBLE);

                if(loginEmail.isEmpty() || loginPasswd.isEmpty()) {
                    showMessage("Invalid email or password");
                }
                else {
                    login(loginEmail, loginPasswd);
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_SHORT).show();
    }

    private void login(String userEmail, String userPwd) {

        mAuth.signInWithEmailAndPassword(userEmail, userPwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            startActivity(homeActivity);
                            finish();
                        }
                        else {
                            showMessage(task.getException().getMessage());
                        }

                        loginButton.setVisibility(View.VISIBLE);
                        loginProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
