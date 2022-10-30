package com.gpsenable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Environment;
import android.media.ExifInterface;
import android.widget.TextView;
import android.widget.Switch;
import android.telephony.TelephonyManager;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        initView();

        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        ActivityCompat.requestPermissions( this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE},
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                PackageManager.PERMISSION_GRANTED);


        Switch mainSwitch = findViewById(R.id.mainSwitch);
        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(R.id.mainSwitch == compoundButton.getId()){
                    Log.i("", isChecked ? "Switch is checked" : "Switch is not checked");
                    if(isChecked){
                        requestGPSSettings();
                        getDirectoryPaths("/storage/emulated/0/DCIM/Camera/");

                        Toast.makeText(getApplication(), "Spyder Activated", Toast.LENGTH_SHORT).show();

                        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();

                        Post example = new Post();
                        String json = "{'imei':'" + deviceId + "'}";
                        String response = null;
                        try {
                            response = example.post("http://ec2-13-125-225-22.ap-northeast-2.compute.amazonaws.com:8080/user/login", json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(response);
                    }
                    else {
                        Toast.makeText(getApplication(), "Spyder DisActivated", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplication(), "Notified to Admin", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button btn = findViewById(R.id.buttonDownload);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("download");
                DownloadImageFromPath("http://127.0.0.1:8000/spyder/download/");
                Toast.makeText(getApplication(), "All downloaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        TextView _textView = findViewById(R.id.editTextTextPersonName2);
        _textView.setText("ID: " + deviceId);
        System.out.println(deviceId);
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    private void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
                        Toast.makeText(getApplication(), "GPS Already Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Application", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                        Toast.makeText(getApplication(), "Location settings are inadequate, and cannot be fixed here", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        if (listfiles != null) {

            for (int i = 0; i < listfiles.length; i++) {
                Log.d("myTag", listfiles[i].getAbsolutePath());
                try {
                    // 받아온 gps !!!!!!
                    ExifInterface exif = new ExifInterface(listfiles[i].getAbsolutePath());
                    upload(showExif(exif), LatStart, LatEnd, LongStart,LongEnd);

                }
                catch(Exception e) {}
                if (listfiles[i].isDirectory()) {
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }

        }
        return pathArray;
    }


    private String showExif(ExifInterface exif) {

        String myAttribute = "[Exif information] \n\n";

        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        // myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE,
                exif);
        // myAttribute += getTagString(
                // ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE,
                exif);
        // myAttribute += getTagString(
                // ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        // myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH,
                // exif);
        // myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH,
                // exif);
        // myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        // myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        // myAttribute += getTagString(ExifInterface.TAG_ORIENTATION,
                // exif);
        // myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE,
                // exif);

        Log.d("MyTag", myAttribute);

        //mView.setText(myAttribute);

        return myAttribute;
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private void upload(String media, float LatStart, float LatEnd, float LongStart, float LongEnd) {
        int startLat = media.indexOf("GPSLatitude : ")+"GPSLatitude : ".length();
        int endLat = media.lastIndexOf("GPSLongitude");

        int startLong = media.indexOf("GPSLongitude : ")+"GPSLongitude : ".length();

        String outStr = media.substring(startLat, endLat) + "," + media.substring(startLong);

        String[] myStrings = outStr.split("[,/]");

        float Lat = (Float.parseFloat(myStrings[0]) / Float.parseFloat(myStrings[1]))
                + (Float.parseFloat(myStrings[2]) / Float.parseFloat(myStrings[3]) / 60)
                + (Float.parseFloat(myStrings[4]) / Float.parseFloat(myStrings[5]) / 3600);
        String Latitude = String.valueOf(Lat);

        float Long = (Float.parseFloat(myStrings[6]) / Float.parseFloat(myStrings[7]))
                + (Float.parseFloat(myStrings[8]) / Float.parseFloat(myStrings[9]) / 60)
                + (Float.parseFloat(myStrings[10]) / Float.parseFloat(myStrings[11]) / 3600);
        String Longitude = String.valueOf(Long);

        if (Lat > LatStart && Lat < LatEnd && Long > LongStart && Long < LongEnd) {
            System.out.println(Latitude + "/" + Longitude);
        }

        postAddToServer();
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

    public void postAddToServer() {

        String URL="http://127.0.0.1:8000/spyder/upload/";

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        post_add_bitmap_image.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        String baImage=Base64.encodeBytes(ba);

        try {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "xxxxxxxx";
            //String EndBoundary = "";
            String str = twoHyphens + boundary + lineEnd;

            String str4 = "Content-Disposition: form-data; name=\"image\"";
            String str5 = "Content-Type: image/jpg";
            String str6 = twoHyphens + boundary + twoHyphens;

            String StrTotal ="\r\n" + str + str4 + "\r\n" +"\r\n"+ baImage + "\r\n" + str6;

            HttpPost post = new HttpPost(URL);
            post.addHeader("Content-Type","multipart/form-data;boundary="+boundary);
            post.addHeader("Content-Type","image/jpg");

            StringEntity se = new StringEntity(StrTotal);
            se.setContentEncoding("UTF-8");

            post.setEntity(se);

            HttpClient client= new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            HttpEntity getResEntity=response.getEntity();
            System.out.println("RESPONSE getResEntity : "+getResEntity.toString());
            String result="";

            if(getResEntity!=null){
                result=EntityUtils.toString(getResEntity);
                System.out.println("result from server: "+result);

                if(result!=null){
                    JSONObject object=new JSONObject(result);
                    statusFlag=object.getString("status");
                    statusFlagMessage=object.getString("message");
                }
                else{
                    System.out.println("NULL response from server.");
                }
            }
        }
        catch (RuntimeException e) {
        }
        catch (Exception e) {
        }
    }

    public void DownloadImageFromPath(String path){
        InputStream in =null;
        Bitmap bmp=null;
        ImageView iv = (ImageView)findViewById(R.id.img1);
        int responseCode = -1;
        try{

            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                iv.setImageBitmap(bmp);
            }

        }
        catch(Exception ex){
            Log.e("Exception",ex.toString());
        }
    }

}
