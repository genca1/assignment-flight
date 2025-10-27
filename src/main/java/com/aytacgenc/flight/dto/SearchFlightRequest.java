package com.aytacgenc.flight.dto;

import com.aytacgenc.flight.helper.DateTimeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.providerB.consumingwebservice.wsdl.SearchRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// SearchFlightRequest.java
public class SearchFlightRequest {
    private String departure;
    private String arrival;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDate;

    // Constructors
    public SearchFlightRequest() {}

    public SearchFlightRequest(String departure, String arrival, LocalDateTime departureDate) {
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
    }

    public SearchFlightRequest(SearchRequest searchRequest) {
        this.arrival = searchRequest.getArrival();
        this.departure = searchRequest.getDeparture();
        DateTimeConverter.toLocalDateTime(searchRequest.getDepartureDate());
        this.departureDate = DateTimeConverter.toLocalDateTime(searchRequest.getDepartureDate());
    }

    // Getters and setters
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }

    public LocalDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDateTime departureDate) { this.departureDate = departureDate; }
}
