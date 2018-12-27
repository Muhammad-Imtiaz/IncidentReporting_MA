package com.example.imtiaz.lab_tasks.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imtiaz.lab_tasks.LanguageChange;
import com.example.imtiaz.lab_tasks.Profile_Setting;
import com.example.imtiaz.lab_tasks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static maes.tech.intentanim.CustomIntent.customType;

public class Settings extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mName, mStatus, mProfile, mLocals, mThemes, mHelp, mAbout;
    private ImageView mImageIcon;
    private RelativeLayout profileSection;

    private DatabaseReference mRootRef;
    private FirebaseUser mRootUser;

    private String name, status, profile_Url;

    private static final String TAG = "Settings";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initBasicViews();


        profileSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, Profile_Setting.class));
                customType(Settings.this,"bottom-to-up");
            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, Profile_Setting.class));
                customType(Settings.this,"bottom-to-up");
            }
        });

        mLocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, LanguageChange.class));
                customType(Settings.this,"bottom-to-up");
            }
        });

        mThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, ThemeChange.class));
                customType(Settings.this,"bottom-to-up");
            }
        });

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, Help.class));
                customType(Settings.this,"bottom-to-up");
            }
        });

        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, About.class));
                customType(Settings.this,"bottom-to-up");
            }
        });


    }

    private void initBasicViews() {
        profileSection = (RelativeLayout) findViewById(R.id.profileSection);
        mName = (TextView) findViewById(R.id.nameSetting);
        mStatus = (TextView) findViewById(R.id.status_setting);
        mProfile = (TextView) findViewById(R.id.profile_settings);
        mLocals = (TextView) findViewById(R.id.local_settings);
        mThemes = (TextView) findViewById(R.id.theme_settings);
        mHelp = (TextView) findViewById(R.id.help_settings);
        mAbout = (TextView) findViewById(R.id.about_settings);
        mImageIcon = (ImageView) findViewById(R.id.imageIconSetting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getting data from database
        mRootUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mRootUser.getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference("Users");
        mRootRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue().toString();
                status = dataSnapshot.child("Status").getValue().toString();
                profile_Url = dataSnapshot.child("Thumbnail_Image").getValue().toString();

                Log.i(TAG, "onCreate: User data" + name + " " + status + " " + profile_Url);
                mName.setText(name);
                mStatus.setText(status);
                if (profile_Url.equals("default")){
                    Glide.with(Settings.this).load(R.drawable.ic_profile_thumbnail).apply(RequestOptions.circleCropTransform()).into(mImageIcon);
                }else {
                    Glide.with(Settings.this).load(profile_Url).apply(RequestOptions.circleCropTransform()).into(mImageIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void localsSelected(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.localsSelection);

    }
}
