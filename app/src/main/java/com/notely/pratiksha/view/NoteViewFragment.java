package com.notely.pratiksha.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.notely.pratiksha.R;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pratiksha on 4/12/18.
 */

public class NoteViewFragment extends Fragment {

    private long noteId;
    private String title;
    private String gist;
    private String lastUpdated;

    private TextView gistTV;
    private Activity activity;

    public NoteViewFragment(){
        noteId = -1;
        title = "";
        gist = "";
        lastUpdated = "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            noteId = bundle.getLong("noteId", -1);
            title = bundle.getString("title", "");
            gist = bundle.getString("gist","");
            lastUpdated = bundle.getString("lastUpdated","");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        View rootView = inflater.inflate(R.layout.note_view, container, false);
        gistTV = rootView.findViewById(R.id.gist);
        gistTV.setText(gist);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        ActionBar actionBar= ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setSubtitle(lastUpdated);
        actionBar.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_view,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_edit:
                launchFragment();
               break;

        }

        return true;
    }

    private void launchFragment(){
        FragmentTransaction transaction = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        NoteEditFragment noteEditFragment = new NoteEditFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", title);
        bundle.putString("gist",gist);
        noteEditFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, noteEditFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }



}
