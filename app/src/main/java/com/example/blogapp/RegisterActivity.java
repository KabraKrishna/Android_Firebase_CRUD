package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, confirmPassword;
    private Button registerButton;
    private TextView loginLink;
    private ProgressBar registerProgressBar;
    private FirebaseAuth mAuth;
    private Intent homeActivity;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.regName);
        email = findViewById(R.id.regMail);
        password = findViewById(R.id.regPassword);
        confirmPassword = findViewById(R.id.regPassword2);
        registerButton = findViewById(R.id.regBtn);
        loginLink = findViewById(R.id.text_login);
        registerProgressBar = findViewById(R.id.regProgressBar);
        registerProgressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, Home.class);

        addListners();
    }

    private void addListners() {

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerButton.setVisibility(View.INVISIBLE);
                registerProgressBar.setVisibility(View.VISIBLE);

                final String userName = name.getText().toString();
                final String userEmail = email.getText().toString();
                final String userPwd = password.getText().toString();
                final String confirmPwd = confirmPassword.getText().toString();

                if(userName.isEmpty() || userEmail.isEmpty() || userPwd.isEmpty() || confirmPwd.isEmpty()){
                    showMessage("Invalid or empty input(s)");

                    registerButton.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    registerNewUser(userName, userEmail, userPwd);
                }

            }
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void registerNewUser(String uName, String email, String pwd) {

        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(uName).build();

                            user.updateProfile(request)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if(task1.isSuccessful()) {

                                                registerButton.setVisibility(View.VISIBLE);
                                                registerProgressBar.setVisibility(View.INVISIBLE);

                                                startActivity(homeActivity);
                                                finish();
                                            }
                                            else{
                                                showMessage(task1.getException().getMessage());
                                                registerButton.setVisibility(View.VISIBLE);
                                                registerProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });

                        }
                        else{
                            showMessage(task.getException().getMessage());
                            registerButton.setVisibility(View.VISIBLE);
                            registerProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

}