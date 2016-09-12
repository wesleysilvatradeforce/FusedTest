package com.example.android.fusedteste;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wesleysilva on 9/4/16.
 */

public class FusedService extends Service implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private GoogleApiClient googleClient;
    private LocationRequest locationRequest;
    private Realm realm;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(FusedService.class.getCanonicalName(), "Init Service");

        googleClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5*1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        googleClient.connect();

        RealmConfiguration realmConf = new RealmConfiguration
                .Builder(this)
                .build();
        Realm.setDefaultConfiguration(realmConf);
        
        realm = Realm.getDefaultInstance();

        return START_STICKY;
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleClient,
                locationRequest,
                this
        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(FusedService.class.getCanonicalName(), connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(final Location location) {

        Log.e(FusedService.class.getCanonicalName(), location.getLatitude()+"");
        Log.e(FusedService.class.getCanonicalName(), location.getLongitude()+"");
        Log.e(FusedService.class.getCanonicalName(), "Battery: "+levelBattery()+"");

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                com.example.android.fusedteste.Location l = realm.createObject(
                    com.example.android.fusedteste.Location.class);
                    l.setLat(location.getLatitude());
                    l.setLongi(location.getLongitude());
                    l.setDate(new Date());
                l.setBattery(levelBattery());

            }
        });
    }


    public double capacityBattery() throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        final String NAME_CLASS = "com.android.internal.os.PowerProfile";

        Object batteryClazz = Class.forName(NAME_CLASS).getConstructor(Context.class)
                .newInstance(this);
        Object n = Class
                .forName(NAME_CLASS)
                .getMethod("getAveragePower", java.lang.String.class)
                .invoke(batteryClazz, "battery.capacity");
         return (double) n;
    }

    public float levelBattery(){
        IntentFilter filter  = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        Log.i("BATYERY STATUS", "level: "+level+" -- Scale: "+scale);
        return level/(float) scale;
    }
}
