package com.example.imtiaz.lab_tasks.myadapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.models.Post;

import java.util.ArrayList;

public class MyTimelineAdapter extends RecyclerView.Adapter<MyTimelineAdapter.ViewHolder> {

    private ArrayList<Post> timeline_posts;
    private Context context;

    public MyTimelineAdapter(ArrayList<Post> timeline_posts, Context context) {
        this.context = context;
        this.timeline_posts = timeline_posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.single_post, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post singlePost_Data = timeline_posts.get(viewHolder.getAdapterPosition());

        Glide.with(context).load(singlePost_Data.getThumbnail_image()).apply(RequestOptions.circleCropTransform()).into(viewHolder.mThumbnail_image);
        viewHolder.mName.setText(singlePost_Data.getName());
        viewHolder.mDate.setText(singlePost_Data.getDate());
        viewHolder.mIncident_category.setText(singlePost_Data.getIncidentCategory());
        viewHolder.mAccident_Description.setText(singlePost_Data.getIncidentDescription());
        Glide.with(context).load(singlePost_Data.getIncident_image()).into(viewHolder.mAccident_image);

    }

    @Override
    public int getItemCount() {
        return timeline_posts.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mThumbnail_image;
        private ImageView mAccident_image;
        private TextView mName;
        private TextView mDate;
        private TextView mIncident_category;
        private TextView mAccident_Description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mThumbnail_image = (ImageView) itemView.findViewById(R.id.image_thumbnail_singlePost);
            mAccident_image = (ImageView) itemView.findViewById(R.id.image_incident_singlePost);
            mName = (TextView) itemView.findViewById(R.id.name_singlePost);
            mDate = (TextView) itemView.findViewById(R.id.date_singlePost);
            mIncident_category = (TextView) itemView.findViewById(R.id.type_singlePost);
            mAccident_Description = (TextView) itemView.findViewById(R.id.description_singlePost);

        }
    }

    public void add(Post post){
        timeline_posts.add(post);
        notifyItemInserted(timeline_posts.size()-1);
    }

}
