package com.dyndyn.demo.model;


import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

import java.util.List;

public class User {

    private Long chatId;
    private LatLng latLng;
    private List<PlacesSearchResult> lastPlaces;

    public User() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public List<PlacesSearchResult> getLastPlaces() {
        return lastPlaces;
    }

    public void setLastPlaces(List<PlacesSearchResult> lastPlaces) {
        this.lastPlaces = lastPlaces;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("chatId=").append(chatId);
        sb.append(", latLng=").append(latLng);
        sb.append('}');
        return sb.toString();
    }
}
