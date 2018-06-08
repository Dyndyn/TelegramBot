package com.dyndyn.demo.repository.impl;

import com.google.maps.model.LatLng;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TinkerPopRepositoryImpl {

    private TinkerGraph graph;
    private static String location = "location";

    @Autowired
    public TinkerPopRepositoryImpl(TinkerGraph graph) {
        this.graph = graph;
    }

    public void addOrUpdateUser(Long chatId, LatLng latLng){
        GraphTraversalSource g = graph.traversal();
        if (g.V(chatId).toList().isEmpty()) {
            g.addV("User").property(T.id, chatId).property(location, latLng);
        } else {
            g.V(chatId).property(location, latLng);
        }


    }

    public LatLng getLocation(Long chatId){
        List<Object> objects = graph.traversal().V(chatId).values(location).toList();
        if (objects.isEmpty()){
            return null;
        }
        return (LatLng) objects.get(0);
    }
}
