package com.devbear.location;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;
    private Button button;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6;

    public String getGetLatitude() {
        return getLatitude;
    }

    public void setGetLatitude(String getLatitude) {
        this.getLatitude = getLatitude;
    }

    public String getGetLongitude() {
        return getLongitude;
    }

    public void setGetLongitude(String getLongitude) {
        this.getLongitude = getLongitude;
    }

    public String getGetCountryName() {
        return getCountryName;
    }

    public void setGetCountryName(String getCountryName) {
        this.getCountryName = getCountryName;
    }

    public String getGetLocality() {
        return getLocality;
    }

    public void setGetLocality(String getLocality) {
        this.getLocality = getLocality;
    }

    public String getGetPostalCode() {
        return getPostalCode;
    }

    public void setGetPostalCode(String getPostalCode) {
        this.getPostalCode = getPostalCode;
    }

    public String getGetAddressLine() {
        return getAddressLine;
    }

    public void setGetAddressLine(String getAddressLine) {
        this.getAddressLine = getAddressLine;
    }

    private String getLatitude, getLongitude, getCountryName, getLocality, getPostalCode, getAddressLine;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gpsTracker = new GPSTracker(getApplicationContext());

                new AsyncInsert().execute();

            }
        });
    }

    private void setText() {
        textView1.setText(getGetLatitude());
        textView2.setText(getGetLongitude());
        textView3.setText(getGetCountryName());
        textView4.setText(getGetLocality());
        textView5.setText(getGetPostalCode());
        textView6.setText(getGetAddressLine());
    }

    private void init() {
        button = (Button) findViewById(R.id.button);
        textView1 = (TextView) findViewById(R.id.fieldLatitude);
        textView2 = (TextView) findViewById(R.id.fieldLongitude);
        textView3 = (TextView) findViewById(R.id.fieldCountry);
        textView4 = (TextView) findViewById(R.id.fieldCity);
        textView5 = (TextView) findViewById(R.id.fieldPostalCode);
        textView6 = (TextView) findViewById(R.id.fieldAddressLine);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncInsert extends AsyncTask<String, Integer, String> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pDialog.dismiss();
            Toast.makeText(MainActivity.this, "Insert Fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Insert: Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (gpsTracker.canGetLocation()) {
                getLatitude = String.valueOf(gpsTracker.getLatitude());
                getLongitude = String.valueOf(gpsTracker.getLongitude());
                getCountryName = String.valueOf(gpsTracker.getCountryName(MainActivity.this));
                getLocality = String.valueOf(gpsTracker.getLocality(MainActivity.this));
                getPostalCode = String.valueOf(gpsTracker.getPostalCode(MainActivity.this));
                getAddressLine = String.valueOf(gpsTracker.getAddressLine(MainActivity.this));
                setGetLatitude(getLatitude);
                setGetLongitude(getLongitude);
                setGetCountryName(getCountryName);
                setGetLocality(getLocality);
                setGetPostalCode(getPostalCode);
                setGetAddressLine(getAddressLine);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }

            String sLink = "http://kaenkaew.com/adb/db.php";
//            String sLink = "http://localhost/project-fuse/db.php";
            String s1 = "strA";
            String s2 = "strB";

            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(sLink);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair(s1, getLatitude));
            nameValuePairs.add(new BasicNameValuePair(s2, getLongitude));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                } else {
                    onCancelled();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("New record created successfully")) {
                pDialog.dismiss();
                setText();
                Toast.makeText(MainActivity.this, "Insert Success", Toast.LENGTH_SHORT).show();
            } else {
                onCancelled();
            }
        }

    }
}
