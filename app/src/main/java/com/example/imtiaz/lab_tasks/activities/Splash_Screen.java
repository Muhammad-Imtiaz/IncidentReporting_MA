package com.example.imtiaz.lab_tasks.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.fragments.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static maes.tech.intentanim.CustomIntent.customType;

public class Splash_Screen extends AppCompatActivity {

    //views
    private ImageView mImageView;

    // constant
    final static int SPLASH_TIME_OUT = 3000;

    // sharedPreferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    // firebase
    private FirebaseAuth mAuth;
    public boolean result;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mImageView = (ImageView) findViewById(R.id.applogo_splash);

        //load animations
        Animation myanimation = AnimationUtils.loadAnimation(this, R.anim.splash_transitions);
        mImageView.startAnimation(myanimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loginUser()){
                    startActivity(new Intent(Splash_Screen.this, Home.class));
                }else {
                    sendToUserAuthentication();
                }
            }
        }, SPLASH_TIME_OUT) ;
    }


    private void sendToUserAuthentication() {
        startActivity(new Intent(this, UserAuthentication.class));
        customType(this, "left-to-right");
        finish();

    }

    public boolean loginUser(){

        mPreferences = this.getSharedPreferences(SignUp.MYPREF_FILE, MODE_PRIVATE);
        boolean isChecked = mPreferences.getBoolean("checked", false);
        String username = mPreferences.getString("username", null);
        String password = mPreferences.getString("password", null);

        if (username == null || password == null || !isChecked){
            return false;
        }else if (isChecked && !username.equals(null) && !password.equals(null)){
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        result = true;
                    }else {
                        result = false;
                    }
                }
            });
        }
        return result;
    }


}
