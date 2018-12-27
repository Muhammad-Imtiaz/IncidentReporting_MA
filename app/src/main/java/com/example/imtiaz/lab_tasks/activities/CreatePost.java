package com.example.imtiaz.lab_tasks.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imtiaz.lab_tasks.MyUtils;
import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.models.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static maes.tech.intentanim.CustomIntent.customType;

public class CreatePost extends AppCompatActivity{

    private static final int MAP_RESULT_REQUEST_CODE = 1;
    private static final String TAG = "CreatePost";
    // views
    private Toolbar mToolbar;
    private TextView mName;
    private EditText mDescription_Incident;
    private ImageView mImage_Incident, mThumbnail_image;
    private Spinner mCategory_Incident;
    private TextView mDate;
    private Button mPost_btn, mSelectLocation;
    private ProgressDialog progressDialog;
    //vars
    private double lat, lng;
    String locationTitle;
    private ArrayAdapter<CharSequence> myArrayAdapter;
    private CharSequence[] incident_types = {"Accident", "Fire", "Flood", "Earthquake"};
    private String spinner_item;
    public String uid;

    //firebase
    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mToolbar = (Toolbar) findViewById(R.id.toolBar_create_post);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //referencing views
        init();
        //setViewsValue();

        Intent intent = getIntent();
        byte [] imageBytes = intent.getByteArrayExtra("IMAGE");
        Glide.with(this).load(imageBytes).into(mImage_Incident);
        // initializing widgets

        mRootRef = FirebaseDatabase.getInstance().getReference("UsersCredentials");
        mRootRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String profile_url = dataSnapshot.child("profile").getValue().toString();
                mName.setText(username);
                if (profile_url.equals("default")){
                    Glide.with(CreatePost.this)
                            .load(R.drawable.ic_account_circle)
                            .apply(RequestOptions.circleCropTransform().centerInside())
                            .into(mThumbnail_image);
                }else {
                    Glide.with(CreatePost.this)
                            .load(profile_url)
                            .apply(RequestOptions.circleCropTransform().centerCrop().centerInside())
                            .into(mThumbnail_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String date = new SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(new Date());
        mDate.setText(date);


        myArrayAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, incident_types);
        mCategory_Incident.setAdapter(myArrayAdapter);
    }

    private void setViewsValue(){

    }

    private void init() {
        mName = (TextView) findViewById(R.id.name_createPost);
        mThumbnail_image = (ImageView) findViewById(R.id.image_thumbnail_createPost);
        mDescription_Incident = (EditText) findViewById(R.id.description_incident);
        mImage_Incident = (ImageView) findViewById(R.id.image_incident);
        mDate = (TextView) findViewById(R.id.date_createPost);
        mCategory_Incident = (Spinner) findViewById(R.id.category_incident);
        mPost_btn = (Button) findViewById(R.id.postBtn);
        mPost_btn.setVisibility(View.INVISIBLE);
        mSelectLocation = (Button) findViewById(R.id.selectAuthorty);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboard();
        mCategory_Incident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_item = parent.getItemAtPosition(position).toString();
                Toast.makeText(CreatePost.this, spinner_item+"", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveState();
                Intent intent = new Intent(CreatePost.this, GoogleMaps.class);
                intent.putExtra("type", spinner_item);
                startActivityForResult(intent, MAP_RESULT_REQUEST_CODE);
            }
        });


        mPost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress dialogue
                progressDialog = new ProgressDialog(CreatePost.this);
                progressDialog.setTitle("Reporting");
                progressDialog.setMessage("Please wait while we report your incident");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String username = mName.getText().toString();
                Bitmap incident_image = MyUtils.changeImageViewIntoBitmap(mImage_Incident);
                String date = mDate.getText().toString();
                String type = spinner_item;
                String description = mDescription_Incident.getText().toString();

                saveIntoFirebase(username, date, type, description, incident_image, lat, lng, locationTitle);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: called...");
        if (resultCode == RESULT_OK && requestCode==MAP_RESULT_REQUEST_CODE){
            Log.i(TAG, "onActivityResult: data" + data.getDoubleExtra("lat", 0));
            // setting visibility true
            mPost_btn.setVisibility(View.VISIBLE);
            //getting selected location data
            lat = data.getDoubleExtra("Lat", 0);
            lng = data.getDoubleExtra("Long", 0);
            locationTitle = data.getStringExtra("Title");
            Log.i(TAG, "onActivityResult: map data" + lat + " " + locationTitle);

        }
    }

    /*private void saveState(){
        bundle.putString("description", mDescription_Incident.getText().toString());
        bundle.putString("type", spinner_item);
    }*/


    private void saveIntoFirebase(final String name, final String date, final String type, final String des,
                                  Bitmap fullImage, final double lat, final double lng, final String locationTitle){

        // setting image storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference filePath = mStorageRef.child("PostsImages").child(type)
                .child(uid).child(new Random().nextInt(15)+"").child("post_image.jpg");
        final Uri imageUri = MyUtils.getImageUri(this, fullImage);
        UploadTask uploadTask = filePath.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // got incident image url
                    final String incidentImageUrl = downloadUri.toString();
                    Log.i("IncidentImageUrl", incidentImageUrl);

                    // getting user profile image link
                    DatabaseReference mProfileImageRef = FirebaseDatabase.getInstance().getReference("UsersCredentials");
                    mProfileImageRef.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String profileUrl = dataSnapshot.child("profile").getValue().toString();

                            // saving post
                            mRootRef = FirebaseDatabase.getInstance().getReference("AllPosts").push();
                            final String postKey = mRootRef.getKey();
                            Log.i(TAG, "onDataChange: Post key" + postKey);
                            mRootRef.keepSynced(true);

                            Post post = new Post(profileUrl, name, date, type, des, incidentImageUrl, lat, lng, locationTitle);
                            mRootRef.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // setting postValues in different Nodes
                                        currentUserPost(postKey);
                                        selectedAuthorty(type, postKey, locationTitle);
                                        allPostBySingleAuth(type, postKey);
                                        progressDialog.dismiss();
                                        Toast.makeText(CreatePost.this, "Post data has been submitted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CreatePost.this, Home.class));

                                    }else {
                                        progressDialog.hide();
                                        Toast.makeText(CreatePost.this, "Error while submitting post", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                } else {
                    Toast.makeText(CreatePost.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void currentUserPost(String key){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UsersPost");
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String extensionRemoved = userEmail.split("\\.")[0];
        Log.i(TAG, "currentUserPost: User email" + userEmail);
        Log.i(TAG, "currentUserPost: Post key" + key);
        int randValue = random();
        userRef.child(extensionRemoved).child(randValue+"").setValue(key);
    }

    private void selectedAuthorty(String incidentType, String key, String authName){
        DatabaseReference authRef = FirebaseDatabase.getInstance().getReference();
        String authorty;
        switch (incidentType){
            case "Accident":
                authorty = "Hospitals";
                break;
            case "Fire":
                authorty = "Firebargade";
                break;
            case "Flood":
                authorty = "Flood";
                break;
            case "Earthquak":
                authorty = "Earthquak";
                break;
            default:
                authorty = null;
        }

        authRef.child("Individual_"+authorty+"_Posts").child(authName).child(random()+"").setValue(key);

    }

    private void allPostBySingleAuth(String incidentType, String key){
        DatabaseReference authRef = FirebaseDatabase.getInstance().getReference();
        Log.i(TAG, "allPostBySingleAuth: Post key" + key);
        String authorty;
        switch (incidentType){
            case "Accident":
                authorty = "Hospitals";
                break;
            case "Fire":
                authorty = "Firebargade";
                break;
            case "Flood":
                authorty = "Flood";
                break;
            case "Earthquak":
                authorty = "Earthquak";
                break;
            default:
                authorty = null;
        }
        authRef.child("All_"+authorty+"_Posts").child(random()+"").setValue(key);
    }

    private void hideSoftKeyboard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    @Override
    public void onBackPressed () {

        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePost.this);
        builder.setMessage(R.string.discard_message);
        builder.setTitle(R.string.discard);
        builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(CreatePost.this, Home.class));

                customType(CreatePost.this, "right-to-left");
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        builder.show();

    }

    public int random(){
        Random rand = new Random();
        return rand.nextInt();
    }

}