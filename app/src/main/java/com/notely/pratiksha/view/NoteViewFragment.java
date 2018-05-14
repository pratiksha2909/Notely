package com.notely.pratiksha.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import com.notely.pratiksha.R;
import com.notely.pratiksha.Utils;
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

    private View rootView;

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
        rootView = inflater.inflate(R.layout.note_view, container, false);
        gistTV = rootView.findViewById(R.id.gist);
        gistTV.setText(gist);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        //setting the toolbar menu items with relevant data and setting the back navigation as well
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextAppearance(activity, R.style.toolbarTitlestyle);
        toolbar.setSubtitleTextAppearance(activity, R.style.toolbarSubtitlestyle);
        toolbar.setSubtitle(lastUpdated);
        toolbar.setPadding(0,Utils.convertToDp(20, activity),0,0);
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
                launchNoteEditFragment();
               break;

        }

        return true;
    }

    private void launchNoteEditFragment(){
        NoteEditFragment noteEditFragment = new NoteEditFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", noteId);
        bundle.putString("title", title);
        bundle.putString("gist",gist);
        noteEditFragment.setArguments(bundle);

        Utils.launchFragment(activity, noteEditFragment, "NoteViewFragment");
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

}
