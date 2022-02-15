package com.demo.mapsaddress;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button btLocation, openMap;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btLocation = findViewById(R.id.bt_location);
        openMap = findViewById(R.id.openMap);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check permissions
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocations();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });


    }

    private void getLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this,
                            Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        textView1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Latitude :</b><br></font>"
                                        + addresses.get(0).getLatitude()
                        ));
                        textView2.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Longitude :</b><br></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        textView3.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Country Name :</b><br></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        textView4.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Locality :</b><br></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        textView5.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Address :</b><br></font>"
                                        + addresses.get(0).getAddressLine(0)
                        ));
                        openMap.setVisibility(View.VISIBLE);
                        openMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String strUri = "http://maps.google.com/maps?q=loc:" + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude() + " (" + addresses.get(0).getAddressLine(0) + ")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                                Log.d("okkk", strUri);

                                startActivity(intent);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }




    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);

                getLocations();

                Toast.makeText(MainActivity.this, "call Address every 10 sec",
                        Toast.LENGTH_SHORT).show();
            }
        }, delay);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

}