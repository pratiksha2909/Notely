package com.notely.pratiksha.view;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.notely.pratiksha.R;
import com.notely.pratiksha.Utils;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pratiksha on 4/12/18.
 */

public class NoteListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Activity activity;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    public static boolean isStarFilterOn = false;
    public static boolean isFavFilterOn = false;

    private List<Notely> notesToDelete = new ArrayList<>();
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.note_list, container, false);
        activity = getActivity();
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        emptyView = (TextView) rootView.findViewById(R.id.emptyView);

        //setting the recycler view
        recyclerView = rootView.findViewById(R.id.noteListView);
        recyclerViewAdapter = new RecyclerViewAdapter(activity);
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);

        //for swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        FloatingActionButton addButton = rootView.findViewById(R.id.add);
        handleClick(addButton);

        isStarFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isStarFilterOn", false);
        isFavFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isFavFilterOn", false);

        //if filter is on, filter the recycler view adapter as well
        if(!isFavFilterOn && !isStarFilterOn) {
            recyclerViewAdapter.getFilter().filter("A");            //display entire list
        }
        else {
            recyclerViewAdapter.getFilter().filter(Utils.getFilterSting(isStarFilterOn, isFavFilterOn));
        }

        //for showing empty view if there are no notes to show
        if(DataManager.getInstance(activity).getAllNotes().size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }
        setHasOptionsMenu(true);
        return rootView;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        activity.findViewById(R.id.titleText).setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setPadding(0,Utils.convertToDp(30, activity),0,0);
        activity.getMenuInflater().inflate(R.menu.menu_main, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        initializeDrawerMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.menu_item_filter:
                drawerLayout.openDrawer(GravityCompat.END);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
           final Notely noteToDelete = DataManager.getInstance(activity).getAllNotes().get(position);
            notesToDelete.add(noteToDelete);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Note Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            notesToDelete.remove(noteToDelete);
                            DataManager.getInstance(activity).getAllNotes().add(position, noteToDelete);
                            recyclerViewAdapter.notifyItemInserted(position);
                            recyclerView.scrollToPosition(position);
                        }
                    });

            //when the snackbar is dismissed without pressing undo button, delete the note from database.
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event != DISMISS_EVENT_ACTION) {
                        for(Notely note: notesToDelete){
                            new DeleteNoteTask(activity, note.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        notesToDelete.clear();
                    }
                    snackbar.removeCallback(this);

                }


            });

            snackbar.show();
            DataManager.getInstance(activity).getAllNotes().remove(position);
            recyclerViewAdapter.notifyItemRemoved(position);

            if(DataManager.getInstance(activity).getAllNotes().size() == 0){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }

        }
    }

    private void initializeDrawerMenu(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        ImageView filterIV = (ImageView) drawerLayout.findViewById(R.id.filterDL);
        final ImageView heartedIV = (ImageView) drawerLayout.findViewById(R.id.heartedDL);
        final ImageView favouriteIV = (ImageView) drawerLayout.findViewById(R.id.favouriteDL);
        Button applyButton = (Button) drawerLayout.findViewById(R.id.applyButton);
        final TextView heartedTV = (TextView) drawerLayout.findViewById(R.id.heartedTV);
        final TextView favouriteTV = (TextView) drawerLayout.findViewById(R.id.favouriteTV);

        drawerToggle = new android.support.v7.app.ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isStarFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isStarFilterOn", false);
                isFavFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isFavFilterOn", false);
                updateDrawerIcons();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isStarFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isStarFilterOn", false);
                isFavFilterOn = Utils.getFromSharedPreference(activity, "filter-state","isFavFilterOn", false);
            }
        };


        drawerLayout.setDrawerListener(drawerToggle);

        filterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavFilterOn = false;
                isStarFilterOn = false;
                updateDrawerIcons();
            }
        });

        heartedIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavFilterOn = !isFavFilterOn;
                updateDrawerIcons();
            }
        });

        favouriteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStarFilterOn = !isStarFilterOn;
                updateDrawerIcons();

            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //since its just a boolean getting saved.. we dont need to delegate this task to a separate thread.
                Utils.saveInSharedPreference(activity, "filter-state", "isStarFilterOn", isStarFilterOn );
                Utils.saveInSharedPreference(activity, "filter-state", "isFavFilterOn", isFavFilterOn );

                if(!isFavFilterOn && !isStarFilterOn) {
                    recyclerViewAdapter.getFilter().filter("A");            //display entire list
                }
                else {
                    recyclerViewAdapter.getFilter().filter(Utils.getFilterSting(isStarFilterOn, isFavFilterOn));
                }
                drawerLayout.closeDrawers();
            }
        });

        updateDrawerIcons();

    }

    private void updateDrawerIcons(){

        final ImageView heartedIV = (ImageView) drawerLayout.findViewById(R.id.heartedDL);
        final ImageView favouriteIV = (ImageView) drawerLayout.findViewById(R.id.favouriteDL);
        final TextView heartedTV = (TextView) drawerLayout.findViewById(R.id.heartedTV);
        final TextView favouriteTV = (TextView) drawerLayout.findViewById(R.id.favouriteTV);

        if(isFavFilterOn){
            heartedTV.setTextColor(Color.parseColor("#00FFCC"));
            heartedIV.setImageResource(R.drawable.ic_tick_selected);
        }
        else{
            heartedTV.setTextColor(Color.parseColor("#FFFFFF"));
            heartedIV.setImageResource(R.drawable.ic_tick);
        }

        if(isStarFilterOn){
            favouriteTV.setTextColor(Color.parseColor("#00FFCC"));
            favouriteIV.setImageResource(R.drawable.ic_tick_selected);
        }
        else{
            favouriteTV.setTextColor(Color.parseColor("#FFFFFF"));
            favouriteIV.setImageResource(R.drawable.ic_tick);
        }
    }

    private void handleClick(FloatingActionButton addButton){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNoteEditFragment();
            }
        });

    }


    private void launchNoteEditFragment(){

        NoteEditFragment noteEditFragment = new NoteEditFragment();
        Utils.launchFragment(activity, noteEditFragment, "NoteListFragment");

    }



    //for smooth transitioning of fragments
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), nextAnim);

        //using hardware layer to make the animation stutter free
        getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {

                //setting the layer type back to none after animation
                //getView().setLayerType(View.LAYER_TYPE_NONE, null);       //TODO: CRASHING HERE!
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return anim;
    }


    private class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

        //keeping the weak refernce to activity to avoid memory leak problem!
        private WeakReference<Activity> weakReference;
        private long noteId;

        public DeleteNoteTask(Activity activity, long noteId) {
            this.noteId = noteId;
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
            dataManager.deleteNote(noteId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(null);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }




}
