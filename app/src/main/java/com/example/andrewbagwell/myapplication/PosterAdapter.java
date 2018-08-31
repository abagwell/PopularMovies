package com.example.andrewbagwell.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ImageViewHolder>{

    private int mNumberPosters;
    private ArrayList<String> urlStringArray;
    private HashMap<String, String> mIdMap;
    private View.OnClickListener myOnClickListener;

    public PosterAdapter (Context context, int numberOfPosters, ArrayList<String> urlStrings, HashMap<String, String> imageToIdMap) {

        mNumberPosters = numberOfPosters;
        urlStringArray = urlStrings;
        mIdMap = imageToIdMap;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        Context context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForPoster = R.layout.poster_item;

        View view = inflater.inflate(layoutIdForPoster, viewGroup, false);

        view.setOnClickListener(myOnClickListener);

        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {


        imageViewHolder.listImageView.setTag(mIdMap.get(urlStringArray.get(i)));

        Picasso.with(imageViewHolder.listImageView.getContext()).load(urlStringArray.get(i)).into(imageViewHolder.listImageView);

        imageViewHolder.listImageView.setOnClickListener(new MyOnClickListener());

    }

    @Override
    public int getItemCount() {

        return mNumberPosters;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView listImageView;

        public ImageViewHolder(View itemView) {

            super(itemView);

            listImageView = itemView.findViewById(R.id.iv_poster);

        }

    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent newIntent = new Intent(view.getContext(), MovieDetailsActivity.class);

            newIntent.putExtra(Intent.EXTRA_TEXT, (String)view.getTag());

            view.getContext().startActivity(newIntent);


        }
    }
}
