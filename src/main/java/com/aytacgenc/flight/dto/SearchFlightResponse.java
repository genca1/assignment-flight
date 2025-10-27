package com.aytacgenc.flight.dto;


import com.providerB.consumingwebservice.wsdl.SearchResult;
import com.providerB.consumingwebservice.wsdl.Flight;
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

    @XmlElement(namespace = "http://flightproviderb.service.com")
    protected boolean hasError;

    // Remove namespace to allow inheritance from parent
    @XmlElement(name = "flight")
    protected List<Flight> flight = new ArrayList<>();

    @XmlElement(namespace = "http://flightproviderb.service.com")
    protected String errorMessage;

    public SearchFlightResponse() {
    }

    public SearchFlightResponse(List<Flight> flights) {
        this.flight = flights;
    }

    public SearchResult toSearchResult() {
        SearchResult result = new SearchResult();
        result.setHasError(this.hasError);
        result.setErrorMessage(this.errorMessage);

        if (this.flight != null) {
            for (Flight customFlight : this.flight) {
                Flight flight = new Flight();
                flight.setFlightNumber(customFlight.getFlightNumber());
                flight.setDeparture(customFlight.getDeparture());
                flight.setArrival(customFlight.getArrival());
                result.getFlightOptions().add(flight);
            }
        }
        return result;
    }

    public boolean isHasError() {
        return this.hasError;
    }

    public void setHasError(boolean value) {
        this.hasError = value;
    }

    public List<Flight> getFlight() {
        if (this.flight == null) {
            this.flight = new ArrayList();
        }

        return this.flight;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}
