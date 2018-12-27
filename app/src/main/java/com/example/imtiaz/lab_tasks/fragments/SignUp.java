package com.example.imtiaz.lab_tasks.fragments;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.activities.Home;
import com.example.imtiaz.lab_tasks.MyUtils;
import com.example.imtiaz.lab_tasks.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;
import static maes.tech.intentanim.CustomIntent.customType;


public class SignUp extends Fragment {



    // views
    private Button mCreateUser;
    private TextInputLayout userTextInputLayout, passTextInputLayout, emailTextInputLayout;
    private AppCompatEditText mUsername, mPassword, mEmail;
    private ProgressDialog progressDialog;

    //firebase
    private DatabaseReference myRootRef;
    private FirebaseAuth mAuth;

    //sharedPreferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mPrefEditor;

    //vars
    public static final String MYPREF_FILE = "CURRENT_USER";


    public SignUp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        initBasicViews(rootView);
        hideSoftKeyboard();

        return rootView;
    }

    private void initBasicViews(View rootView){
        userTextInputLayout = rootView.findViewById(R.id.username_TextInputLayout_signUp);
        passTextInputLayout = rootView.findViewById(R.id.password_textInputlayout_signup);
        emailTextInputLayout = rootView.findViewById(R.id.email_textInputlayout_signUp);
        mUsername = (AppCompatEditText) rootView.findViewById(R.id.username_signUp);
        mPassword = (AppCompatEditText) rootView.findViewById(R.id.password_signUp);
        mEmail = (AppCompatEditText) rootView.findViewById(R.id.email_signUp);
        mCreateUser = (Button) rootView.findViewById(R.id.button_signUp);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUsername.getText().toString().isEmpty()){
                    userTextInputLayout.setErrorEnabled(true);
                    userTextInputLayout.setError("Please enter your username");
                }else {
                    userTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mEmail.getText().toString().isEmpty()){
                    userTextInputLayout.setErrorEnabled(true);
                    userTextInputLayout.setError("Please enter your username");
                }else {
                    userTextInputLayout.setErrorEnabled(false);
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
                    passTextInputLayout.setErrorEnabled(true);
                    passTextInputLayout.setError("Please enter your password");
                }else
                    passTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mPassword.getText().toString().isEmpty()){
                    passTextInputLayout.setErrorEnabled(true);
                    passTextInputLayout.setError("Please enter your password");
                }else
                    passTextInputLayout.setErrorEnabled(false);
            }
        });


        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmail.getText().toString().isEmpty()){
                    emailTextInputLayout.setErrorEnabled(true);
                    emailTextInputLayout.setError("Please enter your email");
                }else {
                    emailTextInputLayout.setErrorEnabled(false);
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
                    emailTextInputLayout.setErrorEnabled(true);
                    emailTextInputLayout.setError("Please enter your email");
                }else {
                    emailTextInputLayout.setErrorEnabled(false);
                }
            }
        });

        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("SignUp");
                progressDialog.setMessage("Please wait while we create your account");
                progressDialog.setCanceledOnTouchOutside(false);

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String name = mUsername.getText().toString();

                if (!MyUtils.isValidEmail(email)){
                    progressDialog.hide();
                    mEmail.setError("Please enter you valid email address");
                    mEmail.requestFocus();
                }
                else if (password.length()<6){
                    progressDialog.hide();
                    mPassword.setError("Password should be at least 6 character");
                    mPassword.requestFocus();
                }else {
                    progressDialog.show();
                    createUser(name, email, password);
                }
            }
        });
    }

    private void createUser(final String name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("User", "createUserWithEmail:success");

                            String uid = mAuth.getCurrentUser().getUid();
                            myRootRef = FirebaseDatabase.getInstance().getReference("UsersCredentials");

                            User user = new User(name, email, "default", "Hi, there..");

                            myRootRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        saveCurrentUser(name, email, password);
                                        sendToHome();
                                        emptyFields();
                                    }else {
                                        progressDialog.hide();
                                        Toast.makeText(getContext(), "Error while saving data into realtime" +
                                                " database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            progressDialog.hide();
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void sendToHome() {

        Intent homeIntent = new Intent(getContext(), Home.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        customType(getContext(),"fadein-to-fadeout");
        getActivity().finish();
    }


    private void emptyFields() {
        mUsername.setText("");
        mEmail.setText("");
        mPassword.setText("");
    }

    // saving current user into shareprefrences
    private void saveCurrentUser(String username, String email, String password){
        mPreferences = getContext().getSharedPreferences(MYPREF_FILE, MODE_PRIVATE);
        mPrefEditor = mPreferences.edit();
        mPrefEditor.putString("username", username);
        mPrefEditor.putString("email", email);
        mPrefEditor.putString("password", password);
        mPrefEditor.commit();
    }


    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
