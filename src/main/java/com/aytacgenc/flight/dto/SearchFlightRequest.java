package com.aytacgenc.flight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SearchFlightRequest {
    private String flightNo;
    private String departure;
    private String arrival;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDate;

    // Constructors
    public SearchFlightRequest() {}

    public SearchFlightRequest(String flightNo, String departure, String arrival, LocalDateTime departureDate) {
        this.flightNo = flightNo;
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
    }

    // Getters and setters only (remove the SearchRequest constructor)
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }
    public LocalDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDateTime departureDate) { this.departureDate = departureDate; }
}

