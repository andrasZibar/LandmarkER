package com.codecool.zibi.landmarker.model;

import android.location.Location;
import android.media.Image;

public class Landmark {
    private Location location;
    private String locationID;
    private String name;
    private String photoID;
    private Image photo;

    public Landmark(double lat, double lon, String locationID, String name, String photoID) {
        this.locationID = locationID;
        this.name = name;
        this.photoID = photoID;
        this.location = new Location("");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
    }

    public Landmark(Location location, String locationID, String name, String photoID) {

        this.location = location;
        this.locationID = locationID;
        this.name = name;
        this.photoID = photoID;
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
