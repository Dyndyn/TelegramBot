package com.dyndyn.demo.repository.impl;

import com.dyndyn.demo.model.User;
import com.dyndyn.demo.model.UserBuilder;
import com.dyndyn.demo.repository.UserRepository;
import com.google.maps.model.LatLng;
import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlacesSearchResult;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.umlg.sqlg.structure.RecordId;
import org.umlg.sqlg.structure.SchemaTable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TinkerPopUserRepositoryImpl implements UserRepository{

    private Graph graph;
    private static String lat = "lat";
    private static String lng = "lng";
    private static String chatId = "chatId";
    private static String edge = "knows";
    private static String lastPlaceUpdate = "lastPlaceUpdate";

    @Autowired
    public TinkerPopUserRepositoryImpl(Graph graph) {
        this.graph = graph;
    }

    @Override
    public User getByChatId(Long chatId) {
        Iterator<Vertex> vertices = graph.traversal().V().has(this.chatId, chatId).toList().iterator();
        if (vertices.hasNext()){
            Vertex vertex = vertices.next();
            UserBuilder builder = new UserBuilder();
            builder.setId((RecordId) vertex.id());
            builder.setLoction(new LatLng(vertex.value(lat), vertex.value(lng))).setChatId(chatId);

            vertex.value(lastPlaceUpdate);
            long hours = ChronoUnit.HOURS.between(
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(vertex.value(lastPlaceUpdate)), ZoneId.systemDefault()),
                    LocalDateTime.now());

            List<PlacesSearchResult> placesSearchResults = new ArrayList<>();
            if (hours < 1) {
                Iterator<Edge> edges = vertex.edges(Direction.BOTH, edge);
                while (edges.hasNext()) {
                    Vertex placeVertex = edges.next().inVertex();
                    PlacesSearchResult placesSearchResult = new PlacesSearchResult();
                    placesSearchResult.name = placeVertex.value("name");
                    placesSearchResult.vicinity = placeVertex.value("vicinity");
                    placesSearchResult.rating = placeVertex.value("rating");
                    placesSearchResult.openingHours = new OpeningHours();
                    placesSearchResult.openingHours.openNow = placeVertex.value("openNow");

                    placesSearchResults.add(placesSearchResult);
                }
            }
            builder.setPlaces(placesSearchResults);
            return builder.build();
        }
        return null;
    }

    @Override
    public void update(User user) {
        GraphTraversalSource g = graph.traversal();
        Vertex userVertex = graph.vertices(user.getId()).next();
        userVertex.property(lat, user.getLatLng().lat,
                lng, user.getLatLng().lng);

        if (user.getLastPlaces() != null){
            userVertex.vertices(Direction.BOTH, edge).remove();
            userVertex.edges(Direction.BOTH, edge).remove();
            addPlaces(g, userVertex, user);
            userVertex.property(lastPlaceUpdate, Instant.now().getEpochSecond());
        }
        g.tx().commit();
    }

    @Override
    public void insert(User user) {
        Vertex userVertex = graph.addVertex(chatId, user.getChatId(), T.label, "User", lat, user.getLatLng().lat,
                lng, user.getLatLng().lng);
        GraphTraversalSource g = graph.traversal();
        if (user.getLastPlaces() != null){
            addPlaces(g, userVertex, user);
            userVertex.property(lastPlaceUpdate, Instant.now().getEpochSecond());
        }

        graph.tx().commit();
    }

    @Override
    public void delete(User user) {

    }

    private void addPlaces(GraphTraversalSource g, Vertex userVertex, User user){
        user.getLastPlaces().stream().forEach(place -> {
            Vertex placeVertex = graph.addVertex(T.label, "Place", "name", place.name, "vicinity", place.vicinity,
                    "rating", place.rating);

            if (place.openingHours != null){
                placeVertex.property("openNow", place.openingHours.openNow);
            } else {
                placeVertex.property("openNow", false);
            }
            g.addE("knows").from(userVertex).to(placeVertex);
        });
    }
}
