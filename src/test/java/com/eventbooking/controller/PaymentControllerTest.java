package com.eventbooking.controller;

import com.eventbooking.dto.payment.PaymentRequest;
import com.eventbooking.dto.payment.PaymentResponse;
import com.eventbooking.service.PaymentService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PaymentControllerTest {
    final String API = "/api/payments";
    final Long BOOKING_ID = 1L;
    final Long PAYMENT_ID = 1L;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PaymentService paymentService;

    PaymentRequest request;
    PaymentResponse response;

    @BeforeEach
    void initData() {
        String CARD_NUMBER = "1111222233334444";
        String CVV = "123";
        String EXPIRY_DATE = "12/25";
        request = new PaymentRequest(BOOKING_ID, CARD_NUMBER, CVV, EXPIRY_DATE);

        response = PaymentResponse.builder()
                .paymentId(PAYMENT_ID)
                .bookingId(BOOKING_ID)
                .build();
    }

    @Test
    @DisplayName("POST /api/payments — returns 200 OK when valid data")
    void processPayment_returns200_whenValidData() throws Exception {
        when(paymentService.processPayment(request)).thenReturn(response);

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "bookingId": 1,
                                        "cardNumber": "1111222233334444",
                                        "cvv": "123",
                                        "expiry": "12/25"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment successful"))
                .andExpect(jsonPath("$.data.bookingId").value(BOOKING_ID))
                .andExpect(jsonPath("$.data.paymentId").value(PAYMENT_ID))
                .andExpect(jsonPath("$.data.amount").value(response.getAmount()))
                .andExpect(jsonPath("$.data.status").value(response.getStatus()));

        verify(paymentService).processPayment(request);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    @DisplayName("POST /api/payments — returns 422 Unprocessable Entity when invalid data")
    void processPayment_returns422_whenInvalidData() throws Exception {
        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "bookingId": null,
                                        "cardNumber": "1234",
                                        "cvv": "12",
                                        "expiry": "1225"
                                    }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(paymentService, never()).processPayment(Mockito.any());
    }
}
