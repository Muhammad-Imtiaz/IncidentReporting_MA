package com.example.imtiaz.lab_tasks.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.models.Post;
import com.example.imtiaz.lab_tasks.myadapters.MyTimelineAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserHome extends Fragment implements Timeline_Frag.TabListener {


    // views
    private View rootView;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyTimelineAdapter mAdapter;
    private ArrayList<Post> myArrayList;

    // firebase
    private DatabaseReference mPostRef;
    private DatabaseReference mCurrentUserPostRef;

    //Vars
    private String uid;

    // listener
    private Timeline_Frag.TabListener tabListener;

    public UserHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.user_home, container, false);
        initBasicViews(rootView);
        return rootView;
    }

    private void initBasicViews(View rootView) {
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_timeline);
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_Timeline);
    }

    @Override
    public void onResume() {
        super.onResume();

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabListener.selectTab(0);
            }
        });

        myArrayList = new ArrayList<Post>();

        layoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyTimelineAdapter(myArrayList, getContext());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        myRecyclerView.setAdapter(mAdapter);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCurrentUserPostRef = FirebaseDatabase.getInstance().getReference("UsersPost");
        mPostRef = FirebaseDatabase.getInstance().getReference("AllPosts");
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        mCurrentUserPostRef.child(uid).addValueEventListener(new ValueEventListener() {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String email = postSnapshot.getKey();
                    if (email.equals(userEmail)){

                    }

                    String postKey = postSnapshot.getKey();


                    Post singlePost = postSnapshot.getValue(Post.class);
                    mAdapter.add(singlePost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
