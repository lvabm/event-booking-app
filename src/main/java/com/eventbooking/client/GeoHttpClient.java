package com.eventbooking.client;

import java.io.IOException;

public interface GeoHttpClient {
    String getJson(String url) throws IOException;
}
