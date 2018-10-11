package com.codecool.zibi.landmarker.model;

import android.location.Location;
import android.media.Image;

public class Landmark {
    private Location location;
    private String locationID;
    private String name;
    private String photoID;
    private Image photo;
    private Location requestMadeFrom;
    public boolean selected = false;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Landmark(double lat, double lon, String locationID, String name, String photoID, double requestLat, double requestLon) {
        this.locationID = locationID;
        this.name = name;
        this.photoID = photoID;
        this.location = new Location("");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
        this.requestMadeFrom = new Location("");
        this.requestMadeFrom.setLatitude(requestLat);
        this.requestMadeFrom.setLongitude(requestLon);
    }

    public Location getRequestMadeFrom() {
        return requestMadeFrom;
    }

    public void setRequestMadeFrom(Location requestMadeFrom) {
        this.requestMadeFrom = requestMadeFrom;
    }

    public Landmark(Location location, String locationID, String name, String photoID, Location requestMadeFrom) {

        this.location = location;
        this.locationID = locationID;
        this.name = name;
        this.photoID = photoID;
        this.requestMadeFrom = requestMadeFrom;

    }

    public Location getLocation() {
        return location;

    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public Landmark(){
    }

}
