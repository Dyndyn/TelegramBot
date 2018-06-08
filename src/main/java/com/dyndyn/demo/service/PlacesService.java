package com.dyndyn.demo.service;

import com.dyndyn.demo.model.User;
import com.dyndyn.demo.repository.UserRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dyndyn.demo.repository.impl.TinkerPopUserRepositoryImpl;

@Service
public class PlacesService {

    private static final Logger logger = LoggerFactory.getLogger(PlacesService.class);
    private static String location = "location";

    private GeoApiContext context;
    private UserService userService;
    private int radius = 100;
    private int delay = 2000;
    private Map<Long, LatLng> users = new HashMap<>();

    @Autowired
    public PlacesService(GeoApiContext context, UserService userService) {
        this.context = context;
        this.userService = userService;
    }

    public List<String> getTypes(Update update) {

        LatLng latLng = new LatLng(update.getMessage().getLocation().getLatitude(),
                update.getMessage().getLocation().getLongitude());

//        users.put(update.getMessage().getChatId(), latLng);
        User user = new User();
        user.setChatId(update.getMessage().getChatId());
        user.setLatLng(latLng);

        userService.addOrUpdate(user);

        List<String> placeTypes = Arrays.stream(PlaceType.values()).map(PlaceType::toString).collect(Collectors.toList());
        logger.info("types {}", placeTypes);
        return getResults(PlacesApi.nearbySearchQuery(context, latLng)
                .radius(radius)).stream()
                .flatMap(item -> Arrays.stream(item.types)).distinct()
                .filter(placeTypes::contains).collect(Collectors.toList());

    }

    public String getPlaces(Update update) {
        User user = userService.getByChatId(update.getCallbackQuery().getMessage().getChatId());
        LatLng latLng = user.getLatLng();


        PlaceType type = PlaceType.valueOf(update.getCallbackQuery().getData().toUpperCase());
        return getResults(PlacesApi.nearbySearchQuery(context, latLng).radius(radius)
                .type(type)).stream().filter(item -> Arrays.stream(item.types)
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
        }

        return sb.append("Рейтинг: ").append(place.rating).append(" / 10\n").toString();
    }

    private List<PlacesSearchResult> getResults(NearbySearchRequest request) {

        PlacesSearchResponse response = null;
        List<PlacesSearchResult> results = new ArrayList<>();
        response = request.awaitIgnoreError();
        Collections.addAll(results, response.results);
        while (response.nextPageToken != null) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            response = PlacesApi.nearbySearchNextPage(context, response.nextPageToken).awaitIgnoreError();
            Collections.addAll(results, response.results);
        }
        logger.info("{} places are found", results.size());

        return results;
    }


    private String toStringPlace(PlacesSearchResult place) {
        final StringBuilder sb = new StringBuilder("Place{");
        sb.append("formattedAddress='").append(place.formattedAddress).append('\'');
        sb.append(", geometry=").append(place.geometry);
        sb.append(", name='").append(place.name).append('\'');
        sb.append(", icon=").append(place.icon);
        sb.append(", placeId='").append(place.placeId).append('\'');
        sb.append(", scope=").append(place.scope);
        sb.append(", rating=").append(place.rating);
        sb.append(", types=").append(Arrays.toString(place.types));
        sb.append(", openingHours=").append(place.openingHours);
        sb.append(", photos=").append(Arrays.toString(place.photos));
        sb.append(", vicinity='").append(place.vicinity).append('\'');
        sb.append(", permanentlyClosed=").append(place.permanentlyClosed);
        sb.append('}');
        return sb.toString();
    }
}
