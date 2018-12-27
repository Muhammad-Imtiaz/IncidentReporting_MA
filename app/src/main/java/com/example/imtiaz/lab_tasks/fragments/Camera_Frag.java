package com.example.imtiaz.lab_tasks.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.activities.CreatePost;
import com.example.imtiaz.lab_tasks.MyUtils;
import com.example.imtiaz.lab_tasks.R;

import java.io.IOException;

public class Camera_Frag extends Fragment implements Timeline_Frag.TabListener{

    private static final String TAG = "Camera_Frag";

    // constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    // widgets
    private Bitmap bitmap;

    // listener
    private Timeline_Frag.TabListener tabListener;


    public Camera_Frag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        takeImageWithChoice();
    }

    private void takeImageWithChoice() {

        final CharSequence [] options = {"Take Photo with Camera", "Pick Image From Gallery", "Cancel"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo To Start...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item] == "Take Photo with Camera"){
                    takeImageWithCamera();
                    Toast.makeText(getContext(), "Take photo selected", Toast.LENGTH_SHORT).show();
                }
                else if (options[item] == "Pick Image From Gallery"){
                    pickImageFromGallery();
                    Toast.makeText(getContext(), "Gallery selected", Toast.LENGTH_SHORT).show();
                }
                else if (options[item] == "Cancel"){
                    dialog.dismiss();
                    tabListener.selectTab(1);
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
            Toast.makeText(getActivity(), "You have not camera installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_IMAGE_CAPTURE){
                bitmap = (Bitmap) data.getExtras().get("data");
                Toast.makeText(getActivity(), bitmap.getByteCount()+"", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_GALLERY){
                Uri uri = data.getData();
               try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byte[] image_byte = image_byte = MyUtils.changeImageIntoByte(bitmap);
            sendImage(image_byte);
        }

    }

    private void sendImage(byte[] image){
        Log.i(TAG, "sendImage: ImageByte" + image);
        Intent sendIntent = new Intent(getContext(), CreatePost.class);
        sendIntent.putExtra("IMAGE", image);
        startActivity(sendIntent);
    }

    @Override
    public void selectTab(int position) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Timeline_Frag.TabListener){
            tabListener = (Timeline_Frag.TabListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabListener = null;
    }

}
