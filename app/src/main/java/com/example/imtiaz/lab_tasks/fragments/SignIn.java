package com.example.imtiaz.lab_tasks.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.activities.ForgotPassword;
import com.example.imtiaz.lab_tasks.activities.Home;
import com.example.imtiaz.lab_tasks.MyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;
import static com.example.imtiaz.lab_tasks.fragments.SignUp.MYPREF_FILE;
import static maes.tech.intentanim.CustomIntent.customType;

public class SignIn extends Fragment {

    // views
    private Button mLogin;
    private TextInputLayout emailTextInput, passwordTextInput;
    private AppCompatEditText mEmail, mPassword;
    private CheckBox mRememberMe;
    private TextView mForgotPassword;
    private ProgressDialog progressDialog;
    private Context context;

    // firebase
    private FirebaseAuth mAuth;

    // shared preferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mPrefEditor;


    public SignIn() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_sign_in, container, false);
        context = getContext();
        // initializing views
        initBasicViews(rootView);
        hideSoftKeyboard();

        return rootView;
    }

    private void initBasicViews(View rootView) {
        emailTextInput = rootView.findViewById(R.id.email_textInputlayout);
        passwordTextInput = rootView.findViewById(R.id.password_textInputlayout);
        mEmail = rootView.findViewById(R.id.email_signIn);
        mPassword = rootView.findViewById(R.id.password_signIn);
        mLogin = rootView.findViewById(R.id.signBtn_signIn);
        mRememberMe = rootView.findViewById(R.id.rememberMe_signIn);
        mForgotPassword = rootView.findViewById(R.id.fogotPass_sign);
    }

    @Override
    public void onResume() {
        super.onResume();

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmail.getText().toString().isEmpty()){
                    emailTextInput.setErrorEnabled(true);
                    emailTextInput.setError("Please enter your email");
                }else {
                    emailTextInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mEmail.getText().toString().isEmpty()){
                    emailTextInput.setErrorEnabled(true);
                    emailTextInput.setError("Please enter your email");
                }else {
                    emailTextInput.setErrorEnabled(false);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPassword.getText().toString().isEmpty()){
                    passwordTextInput.setErrorEnabled(true);
                    passwordTextInput.setError("Please enter your password");
                }else
                    passwordTextInput.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mPassword.getText().toString().isEmpty()){
                    passwordTextInput.setErrorEnabled(true);
                    passwordTextInput.setError("Please enter your password");
                }else
                    passwordTextInput.setErrorEnabled(false);
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Please wait while we signIn you!!!");
                progressDialog.setCanceledOnTouchOutside(false);

                // getting user entered credentials;
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (MyUtils.isValidEmail(email) == false){
                    mEmail.setError("Enter your valid email address");
                    mEmail.requestFocus();
                }
                if (password.length()<6){
                    mPassword.setError("Password must be at least six character");
                    mPassword.requestFocus();
                }else {
                    progressDialog.show();
                    signInUser(email, password);
                }

            }
        });

        mRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mPreferences = getContext().getSharedPreferences(MYPREF_FILE, MODE_PRIVATE);
                mPrefEditor = mPreferences.edit();

                if (isChecked == true){
                    mPrefEditor.putBoolean("checked", true);
                    mPrefEditor.commit();
                }
            }
        });


        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ForgotPassword.class));
                customType(getContext(), "bottom-to-up");
            }
        });
    }



    private void signInUser(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "signInWithEmail:success", Toast.LENGTH_SHORT).show();
                            sendToHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.hide();
                            Toast.makeText(context, "signInWithEmail:failure", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });

    }


    private void sendToHome() {

        Intent homeIntent = new Intent(getContext(), Home.class);
        //homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        customType(getContext(),"left-to-right");
        getActivity().finish();
    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
