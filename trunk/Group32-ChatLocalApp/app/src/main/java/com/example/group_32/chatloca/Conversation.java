package com.example.group_32.chatloca;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by dath on 4/20/18.
 */

public class Conversation { //SOMETHING to merge here: userid -> getdata, store trunk data -> not effectively, waste memory
    private String userIdCreated; // << DAT>>
    private Long timeCreated;
    private String conversationName;
    private String nameOfMeeting;
    private String date;
    private String time;
    private Double longitude;
    private Double latitude;
    private String type;

    public Conversation() {
    }

    public Conversation(String userIdCreated, String nameOfMeeting, String date, String time, Double longitude, Double latitude) {
        this.userIdCreated = userIdCreated;
        this.nameOfMeeting = nameOfMeeting;
        this.date = date;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Conversation(String userIdCreated, String nameOfMeeting, String date, String time) {
        this.userIdCreated = userIdCreated;
        this.nameOfMeeting = nameOfMeeting;
        this.date = date;
        this.time = time;
    }

    public Conversation(String conversationName, String type) {
        this.conversationName = conversationName;
        this.type = type;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLocation(Double longtitude, Double latitude) {
        this.longitude = longtitude;
        this.latitude = latitude;
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getUserIdCreated() {
        return userIdCreated;
    }

    public String getNameOfMeeting() {
        return nameOfMeeting;
    }

    public void setNameOfMeeting(String nameOfMeeting) {
        this.nameOfMeeting = nameOfMeeting;
    }

    public void setUserIdCreated(String userIdCreated) {
        this.userIdCreated = userIdCreated;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }


    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getLatLng() {
        if (this.longitude != null && this.latitude != null)
            return new LatLng(this.latitude, this.longitude);
        return null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
