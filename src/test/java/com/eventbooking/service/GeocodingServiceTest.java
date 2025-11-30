package com.eventbooking.service;

import com.eventbooking.client.GeoHttpClient;
import com.eventbooking.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeocodingServiceTest {

    @Mock
    private GeoHttpClient httpClient;

    @InjectMocks
    private GeocodingService service;

    private BigDecimal[] coords;

    @BeforeEach
    void initData() {
        coords = new BigDecimal[]{
                new BigDecimal("40.7128"),
                new BigDecimal("-74.0060")
        };
    }

    @Test
    @DisplayName("getCoordinates: trả về tọa độ khi JSON hợp lệ")
    void getCoordinates_success() throws Exception {

        String json = """
            [{
               "lat": "40.7128",
               "lon": "-74.0060"
            }]
            """;

        when(httpClient.getJson(anyString())).thenReturn(json);

        Optional<BigDecimal[]> result = service.getCoordinates("New York");

        assertTrue(result.isPresent());
        assertEquals(coords[0], result.get()[0]);
        assertEquals(coords[1], result.get()[1]);
    }

    @Test
    @DisplayName("getCoordinates: trả về Optional.empty khi API trả array rỗng")
    void getCoordinates_emptyList() throws Exception {

        when(httpClient.getJson(anyString())).thenReturn("[]");

        Optional<BigDecimal[]> result = service.getCoordinates("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCoordinates: ném BadRequestException khi IOException xảy ra")
    void getCoordinates_throwException() throws Exception {

        when(httpClient.getJson(anyString())).thenThrow(new IOException("Network error"));

        assertThrows(BadRequestException.class, () -> {
            service.getCoordinates("ABC");
        });
    }
}
