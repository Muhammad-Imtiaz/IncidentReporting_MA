package com.example.imtiaz.lab_tasks.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputEditText mEmail;
    private Button mUpdatePasswordBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_forPass);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmail = (TextInputEditText) findViewById(R.id.email_forgotPass);
        mUpdatePasswordBtn = (Button) findViewById(R.id.updatePassBtn_forPass);

        mUpdatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                sendResetPasswordEmail(email);
            }
        });

    }

    private void sendResetPasswordEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Email has been sent...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassword.this, UserAuthentication.class));
                        }
                    }
                });
    }

}
