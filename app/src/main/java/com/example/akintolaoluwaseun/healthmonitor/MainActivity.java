package com.example.akintolaoluwaseun.healthmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ListView matchesListView;
    private TextView infoBox;
    private ArrayList<Match> matches = new ArrayList<Match>();
    private CharSequence mTitle;
    private String message = "";
    private Boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.matchesListView = (ListView) findViewById(R.id.matchesListView);
        this.infoBox = (TextView) findViewById(R.id.txtInfo);
        this.infoBox.setText(getString(R.string.welcomeMessage));
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        isLoaded = true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (!isLoaded) {
            return;
        }

        String query = "";

        switch (position) {
            case 0:
                message = getString(R.string.selectA);
                query = Queries.selectA;
                break;
            case 1:
                message = getString(R.string.selectAllLow);
                query = Queries.selectAllLow;
                break;
            case 2:
                message = getString(R.string.selectAllHigh);
                query = Queries.selectAllHigh;
                break;
            case 3:
                message = getString(R.string.selectB);
                query = Queries.selectB;
                break;
            case 4:
                message = getString(R.string.selectAll);
                query = Queries.selectAll;
                break;
        }

        if (!query.isEmpty()) {
            this.infoBox.setText("Downloading...");
            matches.clear();
            new com.example.akintolaoluwaseun.healthmonitor.DownloadWebpageTask(new AsyncResult() {
                @Override
                public void onResult(JSONObject object) {
                    processJson(object);
                }
            }).execute(query);
        }
    }

    public void onSectionAttached(int number) {

    }

    private void processJson(JSONObject object) {

        try {
            JSONArray rows = object.getJSONArray("rows");

            for (int r = 0; r < rows.length(); ++r) {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                String date = columns.getJSONObject(0).getString("f");
                String time = columns.getJSONObject(1).getString("f");
                String resultss = columns.getJSONObject(2).getString("v");
                String comment = columns.getJSONObject(3).getString("v");
                Match m = new Match(date, time, resultss, comment);
                matches.add(m);
            }

            final MatchesAdapter adapter = new MatchesAdapter(this, R.layout.match, matches);
            matchesListView.setAdapter(adapter);

            this.infoBox.setText(this.message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
