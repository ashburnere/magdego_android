package com.ashburnere.magdegoandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Main activity of the app. Gets the location of the user and loads the data from the MagdeGo API.
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            this.refresh();
            return true;
        }

        if (id == R.id.action_about) {
            Utils.showAboutDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // connect to google play services
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // disconnect from google play services
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    private void loadData(final Location location) {

//    	final Location location = this.getLocation();
        String stringUrl = null;
        if (location == null) {
            updateStatusLabel("Fehler: Standort konnte nicht ermittelt werden!");
            stringUrl = "http://api.magdego.de/departure-time/location/11.621581/52.11036";
        } else {
            stringUrl = "http://api.magdego.de/departure-time/location/" + location.getLongitude() +
                    "/" + location.getLatitude();
        }

//    	String stringUrl = "http://api.magdego.de/departure-time/location/11.621581/52.11036";

        Log.d(TAG, "API request URL: " + stringUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetDataTask().execute(stringUrl);
        } else {
            updateStatusLabel("Fehler: Keine Datenverbindung verfügbar!");
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask does a GET request and gets the result as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class GetDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return this.getData(urls[0]);
            } catch (IOException e) {
                return "unable to retrieve data. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.startsWith("unable")) {
                updateStatusLabel("Fehler: " + result);
                return;
            }

            Intent intent = new Intent(MainActivity.this, DepartureViewActivity.class);
//          intent.putExtra("EXTRA_MESSAGE", result);

            if (result == null || result.isEmpty() || result.equals("[]")) {
                updateStatusLabel("Fehler: Keine Haltestellendaten verfügbar!");
                return;
            }
            DepartureViewActivity.departureTimes = result;
            startActivity(intent);

            // TODO umwandeln in JSON auch in asyntask
            //  JSONArray json = new JSONArray(result);
            // textView.setText(result);
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String getData(String myurl) throws IOException {
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                //  Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = this.readIt(is, "UTF-8");
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String readIt(InputStream inputStream, String encoding)
                throws IOException {
            return new String(readFully(inputStream), encoding);
        }

        private byte[] readFully(InputStream inputStream)
                throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        }
    }

    private void updateStatusLabel(final String text) {
        TextView loadText = (TextView) findViewById(R.id.load_message);
        loadText.setText(text);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        updateStatusLabel("Fehler: Verbindung zu Google Play Service konnte nicht erstellt werden!");
    }

    private static final int REQUEST_LOCATION = 2;

    @Override
    public void onConnected(Bundle arg0) {
       this.refresh();
    }

    private void refresh() {
        updateStatusLabel(getString(R.string.label_loading));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            /*
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Display UI and wait for user interaction
                // TODO
                Log.d(TAG, "TODO Request permissions from user");
            } else {

                // check Permissions Now
                Log.d(TAG, "Requesting permissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
            */

            // should never happen ebcause user sets the permission during installation
            updateStatusLabel("Fehler: Bitte Recht auf Standortzugriff für die App aktivieren!");
        } else {
            // permission has been granted, continue as usual
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.loadData(mLastLocation);
        }
    }
    // request permission callback
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                try {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    this.loadData(mLastLocation);
                } catch (final SecurityException e) {
                    Log.d(TAG, "Failed getting location permission: "
                            + e.getLocalizedMessage());

                }
            } else {
                // Permission was denied or request was cancelled
                Log.d(this.getClass().getName(), "Failed getting location permission");

            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }


}
