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


public class ProfileAboutChange_Frag extends Fragment {

    private Button mUpdate, mCancel;
    private Context context;
    private EditText status;

    private String status_value;

    private DatabaseReference mRootRef;
    private FirebaseUser current_user;
    private ProgressDialog progressDialog;

    private SharedPreferences mPreferences;


    public ProfileAboutChange_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_profile_about_change, container, false);

        context = getContext();
        mUpdate = (Button) rootView.findViewById(R.id.updateAboutBtn);
        mCancel = (Button) rootView.findViewById(R.id.cancelBtnAbout);
        status = (EditText) rootView.findViewById(R.id.nameChange);


        // setting older name to the field
        mPreferences = getContext().getSharedPreferences(SignUp.MYPREF_FILE, MODE_PRIVATE);
        String oldstatus = mPreferences.getString("STATUS", null);
        status.setText(oldstatus);


        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status_value = status.getText().toString();
                if (status_value.isEmpty()){
                    status.setError("Please enter something...");
                    status.requestFocus();
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Updating");
                progressDialog.setMessage("Please wait while we update your status");
                progressDialog.setCanceledOnTouchOutside(false);

                current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                mRootRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                Map userStatus = new HashMap();
                userStatus.put("Status", status_value);


                mRootRef.updateChildren(userStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            mPreferences.edit().putString("STATUS", status_value).commit();

                            progressDialog.dismiss();
                            Toast.makeText(context, "Status Changed....", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, Profile_Setting.class));
                        }else {
                            progressDialog.hide();
                            Toast.makeText(context, "Error while changing status....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Profile_Setting.class));
            }
        });



        return rootView;
    }

}
