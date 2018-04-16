package com.notely.pratiksha.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.notely.pratiksha.R;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by pratiksha on 4/12/18.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private LayoutInflater inflater = null;
    private Activity activity;
    private DataManager dataManager;

    public RecyclerViewAdapter(Activity activity){
        this.activity = activity;
        dataManager = DataManager.getInstance(activity);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(inflater==null)
            inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new RecyclerViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Notely notely = dataManager.getAllNotes().get(position);
        holder.mTitle.setText(notely.getTitle());
        holder.mGist.setText(notely.getGist());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastUpdatedDate = null;
        try {
            lastUpdatedDate = formatter.parse(notely.getLastUpdated());
        }catch (ParseException e){
            e.printStackTrace();
        }

        holder.mLastUpdated.setText("Last Updated " + getRelativeDateString(lastUpdatedDate));



        if(notely.isStarred()) {
            holder.mStar.setImageResource(R.drawable.ic_star_selected);
        }
        else {
            holder.mStar.setImageResource(R.drawable.ic_star);
        }

        if(notely.isFavourite()) {
            holder.mFav.setImageResource(R.drawable.ic_heart_selected);
        }
        else {
            holder.mFav.setImageResource(R.drawable.ic_heart);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(holder, notely.getId());
            }
        });


        holder.mStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notely.isStarred()){
                    holder.mStar.setImageResource(R.drawable.ic_star);

                }
                else {
                    holder.mStar.setImageResource(R.drawable.ic_star_selected);

                }

                Notely noteToSave = new Notely(notely.getTitle(), notely.getGist(), notely.isFavourite(), !notely.isStarred(), notely.getLastUpdated());
                noteToSave.setId(notely.getId());
                notely.setStarred(!notely.isStarred());

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if ( currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB ) {
                    new SaveNoteTask(activity, noteToSave).execute();
                } else{
                    new SaveNoteTask(activity, noteToSave).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        });

        holder.mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notely.isFavourite()){
                    holder.mFav.setImageResource(R.drawable.ic_heart);

                }
                else {
                    holder.mFav.setImageResource(R.drawable.ic_heart_selected);

                }

                Notely noteToSave = new Notely( notely.getTitle(), notely.getGist(), !notely.isFavourite(), notely.isStarred(), notely.getLastUpdated());
                noteToSave.setId(notely.getId());
                notely.setFavourite(!notely.isFavourite());

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if ( currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB ) {
                    new SaveNoteTask(activity, noteToSave).execute();
                } else{
                    new SaveNoteTask(activity, noteToSave).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if(dataManager.getAllNotes() != null){
            return dataManager.getAllNotes().size();
        }
        return 0;
    }


    private void launchFragment(RecyclerViewHolder holder, long noteId){
        FragmentTransaction transaction = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        NoteViewFragment noteViewFragment = new NoteViewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", holder.mTitle.getText().toString());
        bundle.putString("gist", holder.mGist.getText().toString());
        bundle.putString("lastUpdated", holder.mLastUpdated.getText().toString());
        noteViewFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, noteViewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public  String getRelativeDateString( Date date ) {


        Date now = new Date();
        long hrs = (now.getTime() - date.getTime()) / (1000 * 60 * 60);//in milliSec
        if (hrs <= 0) {
            long mins = (now.getTime() - date.getTime()) / (1000 * 60 );
            return pluralizeString("min", (int) mins) + " ago";
        } else if (hrs <= 12) {
            return pluralizeString("hour", (int) hrs) + " ago";
        } else if (hrs <= 24) {
            return "today "+ pluralizeString("hour", (int) hrs) + " ago";
        } else if (hrs <= 24 * 30) {
            return pluralizeString("day", (int) (hrs / (24))) + " ago";
        } else if (hrs <= 24 * 30 * 12) {
            return pluralizeString("month", (int) (hrs / (24 * 30))) + " ago";
        } else {
            return pluralizeString("year", (int) (hrs / (24 * 30 * 12))) + " ago";
        }
    }



    public  String pluralizeString( String str, int count ) {

        String s = "s";
        if ( count <= 1 ) {
            s = "";
        }
        // Default to 0.
        if (count < 0) {
            count = 0;
        }
        return Integer.toString( count ) + " " + str + s;
    }
    private class SaveNoteTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Activity> weakReference;
        private Notely note;


        public SaveNoteTask(Activity activity, Notely note) {
            this.note = note;
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            //using weak reference so as to avoid memory leak problem in AsyncTask!
            DataManager dataManager = DataManager.getInstance(weakReference.get());
            dataManager.saveNote(note, true);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(null);

        }
    }

}
