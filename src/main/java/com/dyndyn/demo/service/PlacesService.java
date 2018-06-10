package com.dyndyn.demo.service;

import com.dyndyn.demo.model.User;
import com.dyndyn.demo.model.UserBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlacesService {

    private static final Logger logger = LoggerFactory.getLogger(PlacesService.class);

    private GeoApiContext context;
    private UserService userService;
    private int radius = 200;
    private int delay = 2000;

    @Autowired
    public PlacesService(GeoApiContext context, UserService userService) {
        this.context = context;
        this.userService = userService;
    }

    public List<String> getTypes(LatLng latLng, Long chatId) {
        List<String> placeTypes = Arrays.stream(PlaceType.values()).map(PlaceType::toString).collect(Collectors.toList());
        List<PlacesSearchResult> places = getResults(PlacesApi.nearbySearchQuery(context, latLng)
                .radius(radius));

        User user = new UserBuilder().setChatId(chatId)
                .setLoction(latLng).setPlaces(places).build();

        userService.addOrUpdate(user);
        return places.stream()
                .flatMap(item -> Arrays.stream(item.types)).distinct()
                .filter(placeTypes::contains).collect(Collectors.toList());

    }

    public String getPlaces(String placeType, Long chatId) {
        User user = userService.getByChatId(chatId);
        PlaceType type = PlaceType.valueOf(placeType.toUpperCase());

        return user.getLastPlaces().stream().filter(item -> Arrays.stream(item.types)
                .anyMatch(i -> i.equals(type.toUrlValue())))
                .map(this::formatPlace)
                .collect(Collectors.joining("\n"));
    }

    private String formatPlace(PlacesSearchResult place) {
        StringBuilder sb = new StringBuilder(place.name).append("\n").append(place.vicinity).append("\n");
        if (place.openingHours != null) {
            sb.append(place.openingHours.openNow ? "Відкрито" : "Закрито");
            if (place.openingHours.weekdayText.length > 0) {
                sb.append(": ")
                        .append(place.openingHours.weekdayText[LocalDate.now().getDayOfWeek().ordinal()]);
            }
            sb.append("\n");
        }

        return sb.append("Рейтинг: ").append(place.rating).append(" / 10\n").toString();
    }

    private List<PlacesSearchResult> getResults(NearbySearchRequest request) {

        PlacesSearchResponse response = null;
        List<PlacesSearchResult> results = new ArrayList<>();
        response = request.awaitIgnoreError();
        Collections.addAll(results, response.results);

        if (results.size() == 20) {
            while (response.nextPageToken != null) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    logger.error(e.toString());
                }

                response = PlacesApi.nearbySearchNextPage(context, response.nextPageToken).awaitIgnoreError();
                Collections.addAll(results, response.results);
            }
        }
        logger.info("{} places have been found", results.size());

        return results;
    }
}
