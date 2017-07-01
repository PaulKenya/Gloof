package com.gloof.gloof;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import java.util.List;

public class AboutActivity extends AppCompatPreferenceActivity {

    //Enable up button to previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
              case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Configuration of the Screen Layout
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    //Create Instance to start activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    //Turn on up button
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //Run method to configure Screen Layout
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    //Run main XML Resource
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_about, target);
    }

    //Configure fragments to Main XML Resource
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || VersionPreferenceFragment.class.getName().equals(fragmentName)
                || GloofPreferenceFragment.class.getName().equals(fragmentName);
    }

    //Open instance for fragment
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VersionPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_version);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //Open instance for fragment
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GloofPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_gloof_about);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
