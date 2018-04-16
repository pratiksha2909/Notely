package com.notely.pratiksha.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.notely.pratiksha.R;

public class MainActivity extends AppCompatActivity {

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

            //adding fragment to container
            NoteListFragment noteListFragment = new NoteListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, noteListFragment).commit();

        }


        }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ActionBar actionBar=getSupportActionBar();
        /*menu.clear();
        if (actionBar != null) {
            actionBar.hide();
        }*/
        actionBar.setTitle("Notely");
        actionBar.show();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }

    }
}
