package com.example.imtiaz.lab_tasks.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.Profile_Setting;
import com.example.imtiaz.lab_tasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ProfileNameChange_Frag extends Fragment {

    private Button mOk, mCancel;
    private EditText mNewName;
    private Context context;

    private String name_value;

    private DatabaseReference mRootRef;
    private FirebaseUser current_user;
    private ProgressDialog progressDialog;

    private SharedPreferences mPreferences;

    public ProfileNameChange_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_name_change, container, false);
        mNewName = (EditText) view.findViewById(R.id.nameChange);
        mOk = (Button) view.findViewById(R.id.nameOkBtn);
        mCancel = (Button) view.findViewById(R.id.cancelBtnAbout);

        context = getContext();

        // setting older name to the field
        mPreferences = getContext().getSharedPreferences(SignUp.MYPREF_FILE, MODE_PRIVATE);
        String name = mPreferences.getString("USERNAME", null);
        mNewName.setText(name);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Profile_Setting.class));
            }
        });




        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name_value = mNewName.getText().toString();
                if (name_value.isEmpty()){
                    mNewName.setError("Please enter your name...");
                    mNewName.requestFocus();
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Updating");
                progressDialog.setMessage("Please wait while we update your name");
                progressDialog.setCanceledOnTouchOutside(false);

                current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                mRootRef = FirebaseDatabase.getInstance().getReference("Users");

                Map newNameMap = new HashMap<>();
                newNameMap.put("Name", name_value);

                mRootRef.child(uid).updateChildren(newNameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            //update into shared preferences
                            mPreferences.edit().putString("USERNAME", name_value).commit();
                            progressDialog.dismiss();
                            Toast.makeText(context, "Name Changed....", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, Profile_Setting.class));
                        }else {
                            progressDialog.hide();
                            Toast.makeText(context, "Error while changing name....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        return view;
    }

}
