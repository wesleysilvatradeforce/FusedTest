package com.example.android.fusedteste;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wesleysilva on 9/4/16.
 */

public class Location extends RealmObject {

    private double lat;
    private double longi;
    private Date date;
    private double battery;

    public Location(){}

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public Location(double lat, double longi, Date date, double battery ) {
        this.lat = lat;
        this.longi = longi;
        this.date = date;
        this.battery = battery;

    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
