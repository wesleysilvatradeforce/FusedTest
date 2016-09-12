package com.example.android.fusedteste;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.google.android.gms.analytics.internal.zzy.l;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{


    private Realm realm;
    private List<com.example.android.fusedteste.Location> a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, FusedService.class);
        startService(intent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main);

        mapFragment.getMapAsync(this);

        getRealConfig();

        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Date date = new Date();
                date.setHours(00);
                 a = realm.where(com.example.android.fusedteste.Location.class)
                         .greaterThan("date", date)
                                .findAll();
                if(a == null || a.size() == 0 && a.size() <11)
                    return ;
            }
        });
    }

    public void getRealConfig(){
        RealmConfiguration realmConf = new RealmConfiguration
                .Builder(this)
                .build();
        Realm.setDefaultConfiguration(realmConf);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10);
        polylineOptions.color(Color.parseColor("#7B1FA2"));

        //List<com.example.android.fusedteste.Location> b = a.subList(a.size() -10, a.size());

        for(com.example.android.fusedteste.Location l : a){
            polylineOptions.add(new LatLng(l.getLat(), l.getLongi()));
        }
        googleMap.addPolyline(polylineOptions);
    }
}
