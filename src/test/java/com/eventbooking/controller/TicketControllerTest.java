package com.eventbooking.controller;

import com.eventbooking.common.constant.Role;
import com.eventbooking.config.SpringSecurityConfig;
import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.dto.event.EventInfo;
import com.eventbooking.entity.User;
import com.eventbooking.security.JwtAuthenticationFilter;
import com.eventbooking.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@Import(SpringSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class TicketControllerTest {

    private static final String API = "/api/tickets";
    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private List<TicketResponse> ticketsFixture;

    @BeforeEach
    void setupSecurityFilterPassThrough() throws Exception {
        SecurityContextHolder.clearContext();
        // Mock filter để pass through khi có authentication trong SecurityContext
        Mockito.doAnswer(
                        invocation -> {
                            var request = invocation.getArgument(0, jakarta.servlet.http.HttpServletRequest.class);
                            var response =
                                    invocation.getArgument(1, jakarta.servlet.http.HttpServletResponse.class);
                            var chain = invocation.getArgument(2, jakarta.servlet.FilterChain.class);

                            chain.doFilter(request, response);
                            return null;
                        })
                .when(jwtAuthenticationFilter)
                .doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        TicketResponse ticket =
                TicketResponse.builder()
                        .ticketId(1001L)
                        .quantity(2)
                        .status("PAID")
                        .event(
                                EventInfo.builder()
                                        .id(101L)
                                        .title("Art Exhibition")
                                        .dateTime(LocalDateTime.of(2025, 5, 12, 10, 0))
                                        .location("Modern Art Gallery, New York")
                                        .build())
                        .build();
        ticketsFixture = List.of(ticket);
    }


    @Test
    @DisplayName("GET /api/tickets — returns 200 when ROLE_USER")
    void getMyTickets_returns200_whenRoleUser() throws Exception {
        authenticateUser();
        when(ticketService.getMyTickets(USER_ID)).thenReturn(ticketsFixture);

        mockMvc.perform(get(API).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tickets fetched successfully"))
                .andExpect(jsonPath("$.data[0].ticketId").value(1001))
                .andExpect(jsonPath("$.data[0].event.title").value("Art Exhibition"))
                .andExpect(jsonPath("$.data[0].event.dateTime").value("2025-05-12T10:00:00"))
                .andExpect(jsonPath("$.data[0].event.location").value("Modern Art Gallery, New York"))
                .andExpect(jsonPath("$.data[0].quantity").value(2))
                .andExpect(jsonPath("$.data[0].status").value("PAID"));


        verify(ticketService).getMyTickets(USER_ID);
        verifyNoMoreInteractions(ticketService);
    }

    @Test
    @DisplayName("GET /api/tickets — returns 401 when unauthenticated")
    void getMyTickets_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(API).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message").value("Unauthorized – Please login to access this resource"))
                .andExpect(jsonPath("$.success").value(false));

        verify(ticketService, never()).getMyTickets(Mockito.anyLong());
    }
    private void authenticateUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("john.doe@example.com");
        user.setRole(Role.USER);
        
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
