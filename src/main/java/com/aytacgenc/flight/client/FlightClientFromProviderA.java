package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.mapper.FlightRequestMapper;
import com.providerA.consumingwebservice.wsdl.SearchResult;
import com.providerA.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.xml.namespace.QName;
import java.util.List;

@Component
public class FlightClientFromProviderA extends BaseFlightClient {

    private static final String ENDPOINT = "https://assignment-providera-production.up.railway.app/ws";
    private static final String SOAP_ACTION = "http://flightprovidera.service.com/AvailabilitySearchRequest";
    private static final String PROVIDER = "http://flightprovidera.service.com";

    @Autowired
    private FlightRequestMapper flightRequestMapper;

    public List<FlightDTO> getFlightsFromProviderA(SearchRequest searchRequest) {
        QName qName = new QName("http://flightprovidera.service.com", "AvailabilitySearchRequest");
        JAXBElement<SearchRequest> jaxbElement = new JAXBElement<>(qName, SearchRequest.class, searchRequest);
        SearchResult response = sendSoapRequest(ENDPOINT, SOAP_ACTION, jaxbElement, SearchResult.class, PROVIDER);

        return flightRequestMapper.mapListFlightDTO(response.getFlightOptions());
    }
}
