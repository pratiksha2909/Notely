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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.notely.pratiksha.R;
import com.notely.pratiksha.Utils;
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
    private boolean isUndoOn;

    private Activity activity;
    private View rootView;


    public NoteEditFragment(){
        noteId = -1;
        title = "";
        gist = "";
        isUndoOn = false;
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
        rootView = inflater.inflate(R.layout.note_edit, container, false);
        gistET = rootView.findViewById(R.id.gist);
        titleET = rootView.findViewById(R.id.title);
        titleET.setText(title);
        gistET.setText(gist);
        setHasOptionsMenu(true);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        addListeners();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        activity.getMenuInflater().inflate(R.menu.menu_edit, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        activity.findViewById(R.id.titleText).setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_undo:
                undoText();
                break;
            case R.id.action_save:
                saveNote();
                break;
        }

        return true;
    }

    //remove listener and hide keypad when fragment goes in fragment
    @Override
    public void onPause() {
        super.onPause();
        hideKeyPad();
        removeListeners();
    }

    //for smooth transitioning of fragments
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), nextAnim);

        //using hardware layer to make the animation stutter free
        rootView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {

                //setting the layer type back to none after animation
                rootView.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return anim;
    }

    private void addListeners(){
        titleET.addTextChangedListener(titleListener);
        gistET.addTextChangedListener(gistListener);
    }

    private void removeListeners(){
        titleET.removeTextChangedListener(titleListener);
        gistET.removeTextChangedListener(gistListener);
    }

    private void undoText(){
        Pair pair = UndoManager.getInstance().popFromStack();
        if(pair == null) {
            return;
        }

        isUndoOn = true;
        if(pair.getTextId().equals("title")){
            titleET.setText(pair.getText());

            //setting the cursor position
            int position = titleET.length();
            Editable etext = titleET.getText();
            Selection.setSelection(etext, position);
        }

        if(pair.getTextId().equals("gist")){
            gistET.setText(pair.getText());

            //setting the cursor position
            int position = gistET.length();
            Editable etext = gistET.getText();
            Selection.setSelection(etext, position);
        }

    }

    private void saveNote(){
        title = titleET.getText().toString();
        gist = gistET.getText().toString();

        if(title.isEmpty() || gist.isEmpty()){
            Toast.makeText(activity, "Can't save an empty note!", Toast.LENGTH_LONG);
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

    private void launchNoteViewFragment(){

        NoteViewFragment noteViewFragment = new NoteViewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", title);
        bundle.putString("gist",gist);
        bundle.putString("lastUpdated", lastUpdated);
        noteViewFragment.setArguments(bundle);
        Utils.launchFragment(activity, noteViewFragment, "NoteEditFragment");
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

            launchNoteViewFragment();
        }
    }

    private void hideKeyPad(){
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(activity.getCurrentFocus()!=null){
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    //listeners for keeping track of the last strings entered!
    private TextWatcher titleListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int afterLength) {
            if(!isUndoOn)
                UndoManager.getInstance().pushToStack("title",charSequence.toString());
            else
                isUndoOn = false;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher gistListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int afterLength) {
            if(!isUndoOn)
                UndoManager.getInstance().pushToStack("gist",charSequence.toString());
            else
                isUndoOn = false;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };



}
