package com.notely.pratiksha.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.notely.pratiksha.R;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pratiksha on 4/14/18.
 */

public class NoteEditFragment extends Fragment {

    private long noteId;
    private String title;
    private String gist;
    private String lastUpdated;
    private EditText titleET;
    private EditText gistET;

    private Activity activity;


    public NoteEditFragment(){
        noteId = -1;
        title = "";
        gist = "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            noteId = bundle.getLong("noteId", -1);
            title = bundle.getString("title", "");
            gist = bundle.getString("gist","");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        View rootView = inflater.inflate(R.layout.note_edit, container, false);
        gistET = rootView.findViewById(R.id.gist);
        titleET = rootView.findViewById(R.id.title);
        titleET.setText(title);
        gistET.setText(gist);
        setHasOptionsMenu(true);
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_edit,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        ActionBar actionBar= ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setSubtitle("");
        actionBar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            /*case R.id.action_undo:
                break;*/
            case R.id.action_save:
                saveNote();
                break;
        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyPad();
    }

    private void saveNote(){
        title = titleET.getText().toString();
        gist = gistET.getText().toString();

        if(title.isEmpty() || gist.isEmpty()){
            //TODO: show error message AND RETURN
            return;
        }

        lastUpdated = getTime();
        Notely notely = new Notely(title, gist, false, false, lastUpdated);
        boolean isUpdate = false;
        if(noteId != -1){
            notely.setId(noteId);
            isUpdate = true;
        }

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if ( currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB ) {
            new SaveNoteTask(activity, notely, isUpdate).execute();
        } else{
            new SaveNoteTask(activity, notely, isUpdate).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    private String getTime() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = formatter.format(new Date());
        return date;
    }

    private void launchFragment(){
        FragmentTransaction transaction = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        NoteViewFragment noteViewFragment = new NoteViewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", title);
        bundle.putString("gist",gist);
        bundle.putString("lastUpdated", lastUpdated);
        noteViewFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, noteViewFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }




    private class SaveNoteTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Activity> weakReference;
        private Notely note;
        private ProgressDialog dialog;
        private boolean isUpdate;

        public SaveNoteTask(Activity activity, Notely note, boolean isUpdate) {
            this.note = note;
            this.isUpdate = isUpdate;
            dialog = new ProgressDialog(activity);
            weakReference = new WeakReference<>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Saving Note. Please wait..");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //using weak reference so as to avoid memory leak problem in AsyncTask!
            DataManager dataManager = DataManager.getInstance(weakReference.get());
            Notely mNote =  dataManager.saveNote(note, isUpdate);
            if(mNote != null){
                noteId = mNote.getId();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(null);
            dialog.dismiss();

            launchFragment();
        }
    }

    private void hideKeyPad(){
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(activity.getCurrentFocus()!=null){
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch(Exception e) {

        }
    }



}
