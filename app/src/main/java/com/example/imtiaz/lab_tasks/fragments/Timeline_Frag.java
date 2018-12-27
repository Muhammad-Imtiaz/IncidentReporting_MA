package com.example.imtiaz.lab_tasks.fragments;

import android.content.Context;
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

import com.example.imtiaz.lab_tasks.myadapters.MyTimelineAdapter;
import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Timeline_Frag extends Fragment {

    private static final String TAG = "Timeline_Frag";

    // views
    private View rootView;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyTimelineAdapter mAdapter;
    private ArrayList<Post> myArrayList;

    // firebase
    private DatabaseReference mPostRef;

    // listener
    private TabListener tabListener;


    public Timeline_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // initializing vies
        init(rootView);

        myArrayList = new ArrayList<Post>();

        layoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyTimelineAdapter(myArrayList, getContext());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        myRecyclerView.setAdapter(mAdapter);

        mPostRef = FirebaseDatabase.getInstance().getReference("AllPosts");
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        mPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + postSnapshot.getValue());
                    Post singlePost = postSnapshot.getValue(Post.class);
                    Log.i(TAG, "onDataChange: SinglePost" + singlePost.toString());
                    Log.i(TAG, "onDataChange: SIngle post" + singlePost.getName() + " " + singlePost.getLat());
                    mAdapter.add(singlePost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void init(View rootView) {
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

    }



    public interface TabListener{
        public void selectTab(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TabListener){
            tabListener = (TabListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabListener = null;
    }
}
