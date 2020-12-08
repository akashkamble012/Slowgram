package com.akashkamble.slowgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.tech.NfcB;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputLayout email, name, userName, password;
    private Button registerBtn;
    private TextView userLogin;


    private FirebaseAuth mAuth;

    private DatabaseReference mRef;

    private ProgressDialog pd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mRef = FirebaseDatabase.getInstance().getReference();

        pd = new ProgressDialog(this);




        initViews();
        initOnClickListeners();



    }

    private void initOnClickListeners() {
        Log.d(TAG, "initOnClickListeners: Created");

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: create an intent to redirect users from this activity to login activity.
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmail = email.getEditText().getText().toString();
                String txtPassword = password.getEditText().getText().toString();
                String txtName = name.getEditText().getText().toString();
                String txtUserName = userName.getEditText().getText().toString();

                if (TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtUserName) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else if (txtPassword.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Password is to short", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(txtUserName, txtName, txtEmail, txtPassword);
                }



            }
        });

    }

    private void registerUser(String userName, String name, String email, String password) {
        Log.d(TAG, "registerUser: initializing Registration");
        pd.setMessage("PLease wait ");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("email", email);
                        map.put("userName", userName);
                        map.put("bio", "");
                        map.put("imageUrl", "default" );
                        map.put("id", mAuth.getCurrentUser().getUid());



                        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            pd.dismiss();
                                            Toast.makeText(RegisterActivity.this, "update the profile", Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                           startActivity(intent);
                                           finish();
                                        }
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void initViews() {
        Log.d(TAG, "initViews: created");
        email = (TextInputLayout) findViewById(R.id.emailText);
        password = (TextInputLayout) findViewById(R.id.passwordText);
        name = (TextInputLayout) findViewById(R.id.nameText);
        userName = (TextInputLayout) findViewById(R.id.userNameText);
        registerBtn = (Button) findViewById(R.id.register);
        userLogin = (TextView) findViewById(R.id.loginUser);


    }
}