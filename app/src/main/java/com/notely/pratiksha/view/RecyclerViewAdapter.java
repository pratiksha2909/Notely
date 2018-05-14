package com.notely.pratiksha.view;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;


import com.notely.pratiksha.R;
import com.notely.pratiksha.Utils;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pratiksha on 4/12/18.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements Filterable {

    private LayoutInflater inflater = null;
    private Activity activity;
    private DataManager dataManager;
    private List<Notely> filteredData =null;
    private NoteFilter noteFilter = new NoteFilter();

    public RecyclerViewAdapter(Activity activity){
        this.activity = activity;
        dataManager = DataManager.getInstance(activity);
        filteredData = new ArrayList<>(dataManager.getAllNotes());
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
        final Notely notely = filteredData.get(position);
        holder.mTitle.setText(notely.getTitle());
        holder.mGist.setText(notely.getGist());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastUpdatedDate = null;
        try {
            lastUpdatedDate = formatter.parse(notely.getLastUpdated());
        }catch (ParseException e){
            e.printStackTrace();
        }

        holder.mLastUpdated.setText("Last Updated " + Utils.getRelativeDateString(lastUpdatedDate));



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
                launchNoteViewFragment(holder, notely.getId());
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

                String filterString  = Utils.getFilterSting(NoteListFragment.isStarFilterOn, NoteListFragment.isFavFilterOn);
                if(!NoteListFragment.isFavFilterOn && !NoteListFragment.isStarFilterOn) {
                    filterString = "A";
                }
                getFilter().filter(filterString);

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

                String filterString  = Utils.getFilterSting(NoteListFragment.isStarFilterOn, NoteListFragment.isFavFilterOn);
                if(!NoteListFragment.isFavFilterOn && !NoteListFragment.isStarFilterOn) {
                    filterString = "A";
                }
                getFilter().filter(filterString);

            }
        });
    }

    @Override
    public int getItemCount() {

        if(filteredData != null){
            return filteredData.size();
        }

        return 0;
    }




    private void launchNoteViewFragment(RecyclerViewHolder holder, long noteId){

        NoteViewFragment noteViewFragment = new NoteViewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", holder.mTitle.getText().toString());
        bundle.putString("gist", holder.mGist.getText().toString());
        bundle.putString("lastUpdated", holder.mLastUpdated.getText().toString());
        noteViewFragment.setArguments(bundle);
        Utils.launchFragment(activity, noteViewFragment, "NoteListFragment");
    }

    @Override
    public Filter getFilter() {
        return noteFilter;
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

    private class NoteFilter extends Filter{


        //1st character set - starred note; 2nd character set - favourite note; 1st character value A - all notes
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();
            List<Notely> notelyList = new ArrayList<>();

            if(charSequence.charAt(0) == 'A'){
                filterResults.values = dataManager.getAllNotes();
                filterResults.count = dataManager.getAllNotes().size();
                return filterResults;
            }

            for(int i = 0; i < dataManager.getAllNotes().size(); i++){
                Notely notely = dataManager.getAllNotes().get(i);

                boolean cond1 = charSequence.charAt(0) == '1';
                boolean cond2 = charSequence.charAt(1) == '1';

                if(cond1 && cond2){
                     if(notely.isStarred() && notely.isFavourite())
                        notelyList.add(notely);
                }
                else if(cond1 && notely.isStarred()){
                    notelyList.add(notely);
                }
                else if(cond2 && notely.isFavourite()){
                    notelyList.add(notely);
                }

            }

            filterResults.values = notelyList;
            filterResults.count = notelyList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredData = (ArrayList<Notely>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
