package com.notely.pratiksha.view;



import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.notely.pratiksha.R;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pratiksha on 4/12/18.
 */

public class NoteListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Activity activity;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.note_list, container, false);
        activity = getActivity();
       // mDrawerLayout= rootView.findViewById(R.id.drawer);
        recyclerView = rootView.findViewById(R.id.noteListView);
        recyclerViewAdapter = new RecyclerViewAdapter(activity);
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        FloatingActionButton addButton = rootView.findViewById(R.id.add);
        handleClick(addButton);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setTitle("Notely");
        actionBar.setSubtitle("");
        actionBar.show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            Notely note = DataManager.getInstance(activity).getAllNotes().get(position);
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if ( currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB ) {
                new DeleteNoteTask(activity, note.getId() ).execute();
            } else{
                new DeleteNoteTask(activity, note.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }




        }
    }

/*    public void initializeDrawerMenu() {
        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.add_Note, R.string.add_Note) {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }*/

    private void handleClick(FloatingActionButton addButton){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment();
            }
        });

    }


    private void launchFragment(){
        FragmentTransaction transaction = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        NoteEditFragment noteEditFragment = new NoteEditFragment();
        transaction.replace(R.id.fragment_container, noteEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

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
