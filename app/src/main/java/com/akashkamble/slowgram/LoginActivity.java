package com.akashkamble.slowgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputLayout email, password;
    private Button login;
    private TextView registerUser;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initViews();

        initOnClickListeners();
    }

    private void initOnClickListeners() {
        Log.d(TAG, "initOnClickListeners: created");

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt = email.getEditText().getText().toString();
                String passwordTxt = password.getEditText().getText().toString();

                if (TextUtils.isEmpty(emailTxt) || TextUtils.isEmpty(passwordTxt)) {
                    Toast.makeText(LoginActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                }else {
                    loginUser(emailTxt, passwordTxt);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        Log.d(TAG, "loginUser: loging in..");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "update the profile", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Login failed", e.getCause());
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews() {
        Log.d(TAG, "initViews: Created");
        email = (TextInputLayout) findViewById(R.id.emailTextLogin);
        password = (TextInputLayout) findViewById(R.id.passwordTextLogin);
        login = (Button) findViewById(R.id.loginActivityBtn);
        registerUser = (TextView) findViewById(R.id.registerUser);
    }
}