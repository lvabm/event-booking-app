package com.eventbooking.service.impl;

import com.eventbooking.client.GeoHttpClient;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.service.GeocodingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingServiceImpl implements GeocodingService {

    private final GeoHttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<BigDecimal[]> getCoordinates(String location) {

        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" +
                URLEncoder.encode(location, StandardCharsets.UTF_8);

        try {
            String json = httpClient.getJson(url);
            JsonNode arr = mapper.readTree(json);

            if (!arr.isArray() || arr.isEmpty()) return Optional.empty();

            JsonNode obj = arr.get(0);

            BigDecimal lat = new BigDecimal(obj.get("lat").asText());
            BigDecimal lon = new BigDecimal(obj.get("lon").asText());

            return Optional.of(new BigDecimal[]{lat, lon});

        } catch (IOException e) {
            log.error("Error when fetching location {}", location, e);
            throw new BadRequestException("Location not exist");
        }
    }
}
