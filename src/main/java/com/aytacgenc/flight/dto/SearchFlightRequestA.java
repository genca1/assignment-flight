package com.aytacgenc.flight.dto;

import com.providerA.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AvailabilitySearchRequest", namespace = "http://flightprovidera.service.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchFlightRequestA {

    @XmlElement(name = "request", namespace = "http://flightprovidera.service.com", required = true)
    private SearchRequest request;

    // Constructors
    public SearchFlightRequestA() {}

    public SearchFlightRequestA(SearchRequest request) {
        this.request = request;
    }

    // Getters and Setters
    public SearchRequest getRequest() {
        return request;
    }

    public void setRequest(SearchRequest request) {
        this.request = request;
    }
}
