package com.dyndyn.demo.repository.impl;

import com.dyndyn.demo.model.User;
import com.dyndyn.demo.repository.UserRepository;
import com.google.maps.model.LatLng;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class TinkerPopUserRepositoryImpl implements UserRepository{

    private Graph graph;
    private static String lat = "lat";
    private static String lng = "lng";
    private static String chatId = "chatId";

    @Autowired
    public TinkerPopUserRepositoryImpl(Graph graph) {
        this.graph = graph;
    }

    @Override
    public User getByChatId(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        Iterator<Vertex> vertices = graph.traversal().V().has(this.chatId, user.getChatId()).toList().iterator();
        if (vertices.hasNext()){
            Vertex vertex = vertices.next();
            user.setLatLng(new LatLng(vertex.value(lat), vertex.value(lng)));
            return user;
        }
        return user;
    }

    @Override
    public void update(User user) {
        GraphTraversalSource g = graph.traversal();
        g.V().has(chatId, user.getChatId()).property(lat, user.getLatLng().lat,
                lng, user.getLatLng().lng);
        g.tx().commit();
    }

    @Override
    public void insert(User user) {
        graph.addVertex(chatId, user.getChatId(), T.label, "User", lat, user.getLatLng().lat,
                lng, user.getLatLng().lng);
        graph.tx().commit();
    }
}
