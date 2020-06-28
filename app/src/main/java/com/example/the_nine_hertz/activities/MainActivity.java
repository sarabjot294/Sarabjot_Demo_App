package com.example.the_nine_hertz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_nine_hertz.R;
import com.example.the_nine_hertz.helperClasses.AudioRecord;

public class MainActivity extends AppCompatActivity implements LocationListener {
    Button locationBtn, navigationBtn, recordBtn, playBtn;
    TextView currentLocation;
    LocationManager locationManager;
    private static final String TAG = "MainActivity";
    String hawaMahalLat = "26.9239";
    String hawaMahalLong = "75.8267";
    AudioRecord audioRecord;
    boolean recordedSuccessfully = false;
    boolean recordingStarted = false;
    ProgressDialog progressDialog;
    boolean locationCoordinates = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        currentLocation = findViewById(R.id.currentLocation);
        locationBtn = findViewById(R.id.locationBtn);
        navigationBtn = findViewById(R.id.navigationBtn);
        recordBtn = findViewById(R.id.recordBtn);
        playBtn = findViewById(R.id.playBtn);
        progressDialog = new ProgressDialog(MainActivity.this);

        audioRecord = new AudioRecord(MainActivity.this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if(!locationCoordinates) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    progressDialog.setTitle("Current Location Coordinates");
                    progressDialog.setMessage("Getting Coordinates...");
                    progressDialog.show();

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                }
                else
                    Toast.makeText(MainActivity.this,"Coordinates are currently showing", Toast.LENGTH_SHORT).show();
            }
        });

            navigationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigation(MainActivity.this, hawaMahalLat, hawaMahalLong);
                }
            });

            recordBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return recordButton(event);
                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton();
                }
            });

    }

    public boolean recordButton(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            try {
                if(audioRecord.init()) {
                    audioRecord.recordStart();
                    recordingStarted = true;
                    recordBtn.setText("Recording...");
                }
                else {
                    recordingStarted = false;
                    recordBtn.setText("Record (The limit is 1 min)");
                }
            }
            catch (Exception e)
            {
                Log.d(TAG, "Boot up Exception : " + e.toString());
            }
            return true;
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(recordingStarted) {
                audioRecord.recordStop();
                recordedSuccessfully = true;
                recordingStarted = false;
                recordBtn.setText("Record (The limit is 1 min)");
            }
            return true;
        }
        return false;

    }
    public void playButton()
    {
        if(audioRecord.checkPlaying()) {
            Log.d(TAG, "onClick: Audio is being played");
            if(audioRecord.stopPlaying()) {
                Log.d(TAG, "onClick: Audio Stopped");
                playBtn.setText("Play");
                return;
            }
            else
            {
                Log.d(TAG, "onClick: Audio Cannot Stopped");
                return;
            }
        }
        else
            Log.d(TAG, "onClick: Nothing is being played rn");
        if(!recordedSuccessfully) {
            Toast.makeText(MainActivity.this, "Please Record something", Toast.LENGTH_SHORT).show();
        }
        else {
            playBtn.setText("Stop");
            audioRecord.play();
            audioRecord.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setText("Play");
                }
            });
        }
    }

    public void navigation(final Context context, final String lat, final String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationCoordinates = true;
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        currentLocation = findViewById(R.id.currentLocation);
        currentLocation.setText("Current Location : \nLatitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status Changed of " + provider + " to  " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider Disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
