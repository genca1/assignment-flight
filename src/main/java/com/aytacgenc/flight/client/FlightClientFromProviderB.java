package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.LogDTO;
import com.providerB.consumingwebservice.wsdl.Flight;
import com.providerB.consumingwebservice.wsdl.SearchRequest;
import com.providerB.consumingwebservice.wsdl.SearchResult;
import jakarta.xml.bind.JAXBElement;
import org.springframework.stereotype.Component;
import javax.xml.namespace.QName;
import java.util.List;

@Component
public class FlightClientFromProviderB extends BaseFlightClient {

    private static final String ENDPOINT = "https://assignment-providerb-production.up.railway.app/ws";
    private static final String SOAP_ACTION = "http://flightproviderb.service.com/AvailabilitySearchRequest";
    private static final String PROVIDER = "http://flightproviderb.service.com";

    public List<Flight> getFlightsFromProviderB(SearchRequest searchRequest) {
        QName qName = new QName("http://flightproviderb.service.com", "AvailabilitySearchRequest");
        JAXBElement<SearchRequest> jaxbElement = new JAXBElement<>(qName, SearchRequest.class, searchRequest);
        SearchResult response = sendSoapRequest(ENDPOINT, SOAP_ACTION, jaxbElement, SearchResult.class, PROVIDER);
        return response.getFlightOptions();
    }
}
