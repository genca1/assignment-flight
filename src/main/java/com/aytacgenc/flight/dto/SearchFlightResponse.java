package com.aytacgenc.flight.dto;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "AvailabilitySearchResponse", namespace = "http://flightproviderb.service.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilitySearchResponse", propOrder = {
        "hasError",
        "flight",
        "errorMessage"
})
public class SearchFlightResponse {
    private boolean hasError;
    private List<FlightDTO> flights = new ArrayList<>();
    private String errorMessage;

    public SearchFlightResponse() {}

    public SearchFlightResponse(List<FlightDTO> flights) {
        this.flights = flights != null ? flights : new ArrayList<>();
        this.hasError = false;
    }

    public SearchFlightResponse(String errorMessage) {
        this.hasError = true;
        this.errorMessage = errorMessage;
        this.flights = new ArrayList<>();
    }

    // Getters and setters
    public boolean isHasError() { return hasError; }
    public void setHasError(boolean hasError) { this.hasError = hasError; }

    public List<FlightDTO> getFlight() { return flights; }
    public void setFlight(List<FlightDTO> flights) { this.flights = flights; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasError = errorMessage != null && !errorMessage.trim().isEmpty();
    }
}

