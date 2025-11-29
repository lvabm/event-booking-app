package com.eventbooking.client.impl;

import com.eventbooking.client.GeoHttpClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class GeoHttpClientImpl implements GeoHttpClient {

    @Override
    public String getJson(String url) throws IOException {
        return new String(
                URI.create(url)
                        .toURL()
                        .openStream()
                        .readAllBytes()
        );
    }
}
