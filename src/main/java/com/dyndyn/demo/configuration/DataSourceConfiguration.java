package com.dyndyn.demo.configuration;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.umlg.sqlg.structure.SqlgGraph;

@Configuration
public class DataSourceConfiguration {

    @Bean
    public Graph getGraphTraversalSource(){
        return SqlgGraph.open("postgres.properties");
    }
}
