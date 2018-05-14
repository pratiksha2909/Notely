package com.notely.pratiksha.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.notely.pratiksha.R;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fragment_container = findViewById(R.id.fragment_container);

        if (fragment_container != null) {

            //donot add fragment again if the activity is recreated.
            if (savedInstanceState != null) {
                return;
            }

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //adding fragment to container with custom animations to make the transition smooth
            NoteListFragment noteListFragment = new NoteListFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right,
                            R.anim.exit_left,
                            R.anim.enter_left,
                            R.anim.exit_right)
                    .add(R.id.fragment_container, noteListFragment).commit();

        }

        }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {

            //to skip showing NoteEditFragment again from NoteViewFragment on pressing back button!
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if(fragment instanceof NoteViewFragment){
                fragmentManager.popBackStack("NoteListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            else{
                fragmentManager.popBackStack();
            }

        } else {
            finish();
        }

    }

}
