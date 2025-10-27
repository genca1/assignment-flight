package com.aytacgenc.flight.dto;

import com.providerA.consumingwebservice.wsdl.AvailabilitySearchRequest;
import com.providerA.consumingwebservice.wsdl.SearchResult;
import com.providerB.consumingwebservice.wsdl.AvailabilitySearchResponse;
import com.providerB.consumingwebservice.wsdl.Flight;
import com.providerB.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {}

    public AvailabilitySearchRequest createAvailabilitySearchRequest() {
        return new AvailabilitySearchRequest();
    }

    public AvailabilitySearchResponse createAvailabilitySearchResponse() {
        return new AvailabilitySearchResponse();
    }

    public SearchRequest createSearchRequest() {
        return new SearchRequest();
    }

    public SearchResult createSearchResult() {
        return new SearchResult();
    }

    public Flight createFlight() {
        return new Flight();
    }
}