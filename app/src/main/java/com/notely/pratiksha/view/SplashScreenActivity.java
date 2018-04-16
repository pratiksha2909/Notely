package com.notely.pratiksha.view;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.notely.pratiksha.R;
import com.notely.pratiksha.model.DataManager;
import com.notely.pratiksha.model.Notely;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by pratiksha on 4/10/18.
 */

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        fetchLaunchData();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ActionBar actionBar=getSupportActionBar();
        menu.clear();
        if (actionBar != null) {
            actionBar.hide();
        }
        return true;
    }

    private void fetchLaunchData(){

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if ( currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB ) {
                    new FetchLaunchDataTask(SplashScreenActivity.this).execute();
                } else{
                    new FetchLaunchDataTask(SplashScreenActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }, SPLASH_TIME_OUT);



    }

    private class FetchLaunchDataTask extends AsyncTask<Void, Void, Void> {

        WeakReference<Activity> weakReference;

        public FetchLaunchDataTask(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //using weak reference so as to avoid memory leak problem in AsyncTask!
            DataManager dataManager = DataManager.getInstance(weakReference.get());
            dataManager.fetchAllNotes();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(null);
            Activity activity = weakReference.get();
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            activity.finish();
        }
    }

}
