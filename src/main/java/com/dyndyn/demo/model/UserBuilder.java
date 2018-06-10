package com.dyndyn.demo.model;


import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import org.umlg.sqlg.structure.RecordId;

import java.util.List;

public class UserBuilder {

    private User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder setId(RecordId id) {
        user.setId(id);
        return this;
    }

    public UserBuilder setChatId(Long chatId) {
        user.setChatId(chatId);
        return this;
    }

    public UserBuilder setLoction(LatLng loction) {
        user.setLatLng(loction);
        return this;
    }

    public UserBuilder setPlaces(List<PlacesSearchResult> places){
        user.setLastPlaces(places);
        return this;
    }

    public User build(){
        return user;
    }


}
