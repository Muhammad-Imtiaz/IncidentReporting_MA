package com.example.imtiaz.lab_tasks.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.imtiaz.lab_tasks.Profile_Setting;
import com.example.imtiaz.lab_tasks.R;


public class ProfilePhoneChange_Frag extends Fragment {

    private Button mOk, mCancel;
    private EditText oldPhone, newPhone;
    private Context context;
    public ProfilePhoneChange_Frag() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile_email_change, container, false);

        context = getContext();
        mOk = (Button) rootView.findViewById(R.id.changePhoneNumber);
        mCancel = (Button) rootView.findViewById(R.id.cancelChangePhoneNumber);

        oldPhone = (EditText) rootView.findViewById(R.id.oldPhoneNumber);
        newPhone = (EditText) rootView.findViewById(R.id.newPhoneNumber);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Profile_Setting.class));
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Profile_Setting.class));
            }
        });


        return rootView;
    }

}
