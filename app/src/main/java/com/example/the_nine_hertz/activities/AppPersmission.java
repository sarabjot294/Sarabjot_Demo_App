package com.example.the_nine_hertz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.the_nine_hertz.R;

public class AppPersmission extends AppCompatActivity {
    Button appPermissionButton;
    private static final String TAG = "app_permission";
    boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permissions);

        appPermissionButton =findViewById(R.id.button);

        askPermission(AppPersmission.this);

        if(permissionGranted)
            goToNextActivity(AppPersmission.this);

        appPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!permissionGranted) {
                    appPermissionButton.setText("Click to provide App Permissions");
                    Toast.makeText(AppPersmission.this, "Please provide All the Neccessary Permission for the app to work ", Toast.LENGTH_LONG).show();
                    askPermission(AppPersmission.this);
                }
                else
                    goToNextActivity(AppPersmission.this);
            }
        });
    }

    private void goToNextActivity(Context context)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        finish();
    }

    private void askPermission(final Context context) {
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: Permission : " + ActivityCompat.checkSelfPermission(context,permission));
                ActivityCompat.requestPermissions(AppPersmission.this, PERMISSIONS, 1);
                Log.e(TAG, "askPermission: We do not have permission for " + permission);
                return;
            } else {
                Log.d(TAG, "Permission granted for " + permission);
            }
        }
        permissionGranted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode: " + requestCode);

        for(int i=0;i<permissions.length;i++)
        {
            Log.e(TAG, "onRequestPermissionsResult: Permission : " + permissions[i] );
            Log.e(TAG, "onRequestPermissionsResult:value  : " + grantResults[i] );
            if(grantResults[i] == PackageManager.PERMISSION_DENIED)
            {
                appPermissionButton.setText("Click to provide App Permissions");
                return;
            }
        }

        Log.d(TAG, "onRequestPermissionsResult: All Permission Granted");
        appPermissionButton.setText("Proceed");
        permissionGranted = true;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
