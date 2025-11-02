package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.mapper.FlightRequestMapper;
import com.providerA.consumingwebservice.wsdl.SearchResult;
import com.providerA.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.xml.namespace.QName;
import java.util.List;

@Component
public class FlightClientFromProviderA extends BaseFlightClient {

    @Value("${provider.a.url}")
    private String endpoint;

    @Value("${provider.a.action}")
    private String soapAction;

    @Value("${provider.a.name}")
    private String provider;

    @Autowired
    private FlightRequestMapper flightRequestMapper;

    public List<FlightDTO> getFlightsFromProviderA(SearchRequest searchRequest) {
        QName qName = new QName(provider, "AvailabilitySearchRequest");
        JAXBElement<SearchRequest> jaxbElement = new JAXBElement<>(qName, SearchRequest.class, searchRequest);
        SearchResult response = sendSoapRequest(endpoint, soapAction, jaxbElement, SearchResult.class, provider);
        return flightRequestMapper.mapListFlightDTO(response.getFlightOptions());
    }
}
