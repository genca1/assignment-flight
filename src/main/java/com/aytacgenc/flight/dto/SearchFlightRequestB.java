package com.aytacgenc.flight.dto;

import com.providerB.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AvailabilitySearchRequest", namespace = "http://flightproviderb.service.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchFlightRequestB {

    @XmlElement(name = "request", namespace = "http://flightproviderb.service.com", required = true)
    private SearchRequest request;

    // Constructors
    public SearchFlightRequestB() {}

    public SearchFlightRequestB(SearchRequest request) {
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
