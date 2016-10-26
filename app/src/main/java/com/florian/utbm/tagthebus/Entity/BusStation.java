package com.florian.utbm.tagthebus.Entity;

import java.io.Serializable;

/**
 * Created by Florian on 22/10/2016.
 */

public class BusStation implements Serializable{

    private long id;
    private String street_name;
    private String city;

    private float lat;
    private float longi;
    private String furniture;
    private String buse;
    private float distance;

    public BusStation(long id, String street_name, String city, float lat, float longi, String furniture, String buse, float distance) {
        this.id = id;
        this.street_name = street_name;
        this.city = city;
        this.lat = lat;
        this.longi = longi;
        this.furniture = furniture;
        this.buse = buse;
        this.distance = distance;
    }

    public String getStreet_name() {
        return street_name;
    }


    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLongi() {
        return longi;
    }

    public void setLongi(float longi) {
        this.longi = longi;
    }

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public String getBuse() {
        return buse;
    }

    public void setBuse(String buse) {
        this.buse = buse;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BusStation{" +
                "id=" + id +
                ", street_name='" + street_name + '\'' +
                ", city='" + city + '\'' +
                ", lat=" + lat +
                ", longi=" + longi +
                ", furniture='" + furniture + '\'' +
                ", buse='" + buse + '\'' +
                ", distance=" + distance +
                '}';
    }
}
