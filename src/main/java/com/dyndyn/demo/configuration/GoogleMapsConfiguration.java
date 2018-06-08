package com.dyndyn.demo.configuration;


import com.google.maps.GeoApiContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class GoogleMapsConfiguration {

    @Resource
    private Environment environment;

    @Bean
    public GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey(environment.getProperty("google.maps.apiKey"))
                .build();
    }

}
