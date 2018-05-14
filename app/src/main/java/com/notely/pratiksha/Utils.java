package com.notely.pratiksha;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;


import java.util.Date;

/**
 * Created by pratiksha on 5/7/18.
 */

public class Utils {

    public static void launchFragment(Activity activity, Fragment fragment, String tag){
        FragmentTransaction transaction = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();

        //for making fragment transitions smooth
        transaction.setCustomAnimations(R.anim.enter_right,
                R.anim.exit_left,
                R.anim.enter_left,
                R.anim.exit_right);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    //method to convert px to dp
    public static int convertToDp(int pixel, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixel, context.getResources().getDisplayMetrics());
    }


    public static String getFilterSting(boolean isStarFilterOn, boolean isFavFilterOn){
        String filterString = "";
        filterString = (isStarFilterOn ? "1" : "0");
        filterString = filterString +  (isFavFilterOn ? "1" : "0");

        return filterString;

    }

    public static void saveInSharedPreference(Context ctx, String fileName, String key, boolean value ) {

        SharedPreferences mPrefs = ( ctx.getSharedPreferences( fileName, Context.MODE_PRIVATE ) );
        SharedPreferences.Editor e      = mPrefs.edit();
        e.putBoolean( key, value );
        e.apply();
    }

    public static boolean getFromSharedPreference( Context ctx, String fileName, String key, boolean defaultvalue ) {

        SharedPreferences mPrefs = ( ctx.getSharedPreferences( fileName, Context.MODE_PRIVATE ) );
        return mPrefs.getBoolean( key, defaultvalue );
    }

    public static String getRelativeDateString( Date date ) {


        Date now = new Date();
        long hrs = (now.getTime() - date.getTime()) / (1000 * 60 * 60);//in milliSec
        if (hrs <= 0) {
            long mins = (now.getTime() - date.getTime()) / (1000 * 60 );
            return pluralizeString("min", (int) mins) + " ago";
        } else if (hrs <= 12) {
            return pluralizeString("hour", (int) hrs) + " ago";
        } else if (hrs <= 24) {
            return "today "+ pluralizeString("hour", (int) hrs) + " ago";
        } else if (hrs <= 24 * 30) {
            return pluralizeString("day", (int) (hrs / (24))) + " ago";
        } else if (hrs <= 24 * 30 * 12) {
            return pluralizeString("month", (int) (hrs / (24 * 30))) + " ago";
        } else {
            return pluralizeString("year", (int) (hrs / (24 * 30 * 12))) + " ago";
        }
    }



    public static String pluralizeString( String str, int count ) {

        String s = "s";
        if ( count <= 1 ) {
            s = "";
        }
        // Default to 0.
        if (count < 0) {
            count = 0;
        }
        return Integer.toString( count ) + " " + str + s;
    }

}
