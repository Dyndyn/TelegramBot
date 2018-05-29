package com.dyndyn.demo;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Created by roman on 29.05.18.
 */
public class FirstDayTest {

    @Test
    public void test(){
        TinkerGraph graph = TinkerFactory.createModern();
        GraphTraversalSource g = graph.traversal();

        System.out.println(g.V(10).values().toList());

        Vertex v1 = g.addV("person").property(T.id, 10).property("name", "marko")
                .property("age", 29).next();
        Vertex v2 = g.addV("software").property(T.id, 11).property("name", "lop")
                .property("lang", "java").next();
        g.addE("created").from(v1).to(v2).property(T.id, 9).property("weight", 0.4);

        System.out.println(g.V(10).values().toList());

    }

}
