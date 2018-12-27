package com.example.imtiaz.lab_tasks;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imtiaz.lab_tasks.fragments.ProfileAboutChange_Frag;
import com.example.imtiaz.lab_tasks.fragments.ProfileNameChange_Frag;
import com.example.imtiaz.lab_tasks.fragments.ProfilePhoneChange_Frag;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileHome extends Fragment {

    private TextView mName, mEmail, mAbout;
    private ImageView mProfileImage;
    private FloatingActionButton mFb;
    private Button mNameBtn, mEmailBtn, mAboutBtn;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private Bitmap bitmap;

    FragmentManager fm;
    FragmentTransaction ft;

    FirebaseUser firebaseUser;
    String uid;
    private DatabaseReference mRootRef;

    public ProfileHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile_home, container, false);

        mName = (TextView) rootView.findViewById(R.id.nameTV_profile);
        mEmail = (TextView) rootView.findViewById(R.id.emailTV_profile);
        mAbout = (TextView) rootView.findViewById(R.id.aboutTV_profile);

        mFb = (FloatingActionButton) rootView.findViewById(R.id.fb_profile);
        mProfileImage = (ImageView) rootView.findViewById(R.id.thumbnail_profile);
        mNameBtn = (Button) rootView.findViewById(R.id.editNameBtn_Profile);
        mEmailBtn = (Button) rootView.findViewById(R.id.editEmailBtn_Profile);
        mAboutBtn = (Button) rootView.findViewById(R.id.editAboutBtn_Profile);

        fm = getFragmentManager();
        ft = fm.beginTransaction();


        mFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfileImage();
            }
        });

        mNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.replace(R.id.container_profile, new ProfileNameChange_Frag());
                ft.commit();
            }
        });

        mEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.replace(R.id.container_profile, new ProfilePhoneChange_Frag());
                ft.commit();
            }
        });

        mAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.replace(R.id.container_profile, new ProfileAboutChange_Frag());
                ft.commit();
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // loading profile data
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference("Users");
        mRootRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String profile_image = dataSnapshot.child("Thumbnail_Image").getValue().toString();

                Log.i("Credentials", name + "  " + status + "  "  + profile_image);

                mName.setText(name);
                mAbout.setText(status);
                mEmail.setText(firebaseUser.getEmail());
                if(profile_image.equals("default") && getContext() != null){
                    Glide.with(getContext()).load(R.drawable.ic_account_circle).
                            apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }else{
                    Glide.with(getContext()).load(profile_image).
                            apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setProfileImage() {

        final CharSequence[] options= {"Take Photo with Camera", "Pick Image from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add photo...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which] == "Take Photo with Camera"){
                    takeImageWithCamera();
                }
                else if (options[which] == "Pick Image from Gallery"){
                    pickImageFromGallery();
                }else if (options[which] == "Cancel"){
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }


    private void pickImageFromGallery() {
        Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_intent.setType("image/*");
        startActivityForResult(gallery_intent, REQUEST_IMAGE_GALLERY);
    }

    private void takeImageWithCamera() {

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera_intent.resolveActivity(getContext().getPackageManager()) !=  null){
            startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
        }else {
            Toast.makeText(getContext(), "You have not camera installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_IMAGE_CAPTURE){
                bitmap = (Bitmap) data.getExtras().get("data");
                Toast.makeText(getContext(), bitmap.getByteCount()+"", Toast.LENGTH_SHORT).show();
            }
            else if (requestCode == REQUEST_IMAGE_GALLERY){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Toast.makeText(getContext(), bitmap.getByteCount()+"", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveProfileImageIntoFirebase(bitmap);
            Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
        }
    }

    private void saveProfileImageIntoFirebase(Bitmap bitmap) {

        DatabaseReference mRootRef;
        StorageReference mStorage;

        final Uri imageUri = MyUtils.getImageUri(getContext(), bitmap);

        mStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference thumbStorageRef = mStorage.child("Posts").child(uid).child(uid + ".jpg");
        UploadTask uploadTask = thumbStorageRef.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return thumbStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    final String photoUrl = downloadUri.toString();
                    Log.i("PhotoUrl", photoUrl);

                    Map data = new HashMap();
                    data.put("Thumbnail_Image", photoUrl);

                    DatabaseReference mRootRef = mRootRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    mRootRef.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(getContext(), "Image link has been submitted...", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), "Failed to upload image link", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
