package com.notely.pratiksha.view;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.notely.pratiksha.R;

/**
 * Created by pratiksha on 4/13/18.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    public TextView mTitle;
    public TextView mGist;
    public TextView mLastUpdated;
    public ImageView mStar;
    public ImageView mFav;
    public RelativeLayout foregroundView, backgroundView;

    public RecyclerViewHolder(View v) {
        super(v);
        mTitle = v.findViewById(R.id.title);
        mGist = v.findViewById(R.id.gist);
        mLastUpdated =  v.findViewById(R.id.lastUpdated);
        mStar =  v.findViewById(R.id.star);
        mFav =  v.findViewById(R.id.heart);
        foregroundView = v.findViewById(R.id.foreground_view);
        backgroundView = v.findViewById(R.id.background_view);
        //mStar.setColorFilter(Color.argb(255, 255, 255, 255));
    }

}