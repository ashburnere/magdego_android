package com.ashburnere.magdegoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * View to show the departure views in a pager component to let the user swipe from
 * view to view.
 */
public class DepartureViewActivity extends AppCompatActivity {

    public static final String KEY_DEPARTURE_TIMES = "departure_times";
    public static final String KEY_DEPARTURE = "departure";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_LINE = "line";
    public static final String KEY_DELAY = "delay";
    public static final String KEY_DELAY_MINUTES = "minutes";

    // save the latest departure times in a static field because intent has not enough space
    public static String departureTimes;

    // this adapter returns fragments representing the departure times for a station,
    private DepartureFragmentPagerAdapter mDepartureFragmentPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JSONArray data;
        try {
            data = new JSONArray(departureTimes);
            int count = data.length();
            // ViewPager and its adapters use support library
            // fragments, so use getSupportFragmentManager.
            mDepartureFragmentPagerAdapter =
                    new DepartureFragmentPagerAdapter(
                            getSupportFragmentManager(), data);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mDepartureFragmentPagerAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_departure_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh_d) {
            Intent intent = new Intent(DepartureViewActivity.this, MainActivity.class);

            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about_d) {
            Utils.showAboutDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * Pager adapter for the departure fragments
     */
    private class DepartureFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int pagesCount;
        private final JSONArray data;

        /**
         * Default constructor.
         *
         * @param fm
         * @param data
         */
        public DepartureFragmentPagerAdapter(final FragmentManager fm, final JSONArray data) {
            super(fm);
            this.data = data;
            this.pagesCount = data.length();
        }

        @Override
        public Fragment getItem(int i) {
            DepartureTableFragment fragment = new DepartureTableFragment();
            try {
                JSONObject stationData = data.getJSONObject(i);
                if (stationData != null ) {
                    fragment.setData(stationData.getString("station_info"),
                            stationData.getJSONArray(KEY_DEPARTURE_TIMES));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return this.pagesCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                System.err.println(((JSONObject)data.getJSONObject(position)).get("station_info") + "");
                return ((JSONObject)data.getJSONObject(position)).get("station_info") + "";

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return ""+position;
        }
    }

    public static void parseDepartureTimes(final String departureTimesJson) {

        try {
            // departure times are given as JSONArray and not as JSONObject by the magdego api
//	   		 JSONObject json = new JSONObject(departureTimesJson);

            JSONArray json = new JSONArray(departureTimesJson);

            for (int i = 0; i<json.length(); i++) {
                printStationInfos(((JSONObject)json.get(i)).getJSONArray(KEY_DEPARTURE_TIMES));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void printStationInfos(final JSONArray departureTimes) throws JSONException {
        System.out.println("Abfahrt (Echtzeit)  Richtung  Linie ");
        for (int i = 0; i < departureTimes.length(); i++) {
            System.out.print(((JSONObject)departureTimes.getJSONObject(i)).get(KEY_DEPARTURE));
            if (((JSONObject)departureTimes.getJSONObject(i)).has(KEY_DELAY)) {
                System.out.print(" " + ((JSONObject)departureTimes.getJSONObject(i)).get(KEY_DELAY));
            } else {
                System.out.print(" - ");
            }

            System.out.print(" " + ((JSONObject)departureTimes.getJSONObject(i)).get(KEY_DIRECTION));
            System.out.println(" " + ((JSONObject)departureTimes.getJSONObject(i)).get(KEY_LINE));
        }
    }

}
