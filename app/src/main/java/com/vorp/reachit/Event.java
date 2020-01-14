package com.vorp.reachit;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by asd on 2018-07-07.
 */

public class Event {
    private String name;
    private String emoji;
    private LatLng location;

    public Event(String name, String emoji, LatLng location) {
        this.name=name;
        this.emoji=emoji;
        this.location=location;
    }

    public String getEmoji() {
        return emoji;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
