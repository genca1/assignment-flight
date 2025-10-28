package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.dto.SearchFlightRequestB;
import com.providerB.consumingwebservice.wsdl.Flight;
import com.providerB.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.JAXBElement;
import org.springframework.stereotype.Component;
import javax.xml.namespace.QName;
import java.util.List;

@Component
public class FlightClientFromProviderB extends BaseFlightClient {

    private static final String ENDPOINT = "http://localhost:8083/ws";
    private static final String SOAP_ACTION = "http://flightproviderb.service.com/AvailabilitySearchRequest";
    private static final String PROVIDER = "http://flightproviderb.service.com";
    private static final FlightParserConfig PARSER_CONFIG =
            new FlightParserConfig("flightNumber", "departure", "arrival");

    public List<Flight> getFlightsFromProviderB(SearchRequest searchRequest) throws Exception {
        QName qName = new QName("http://flightproviderb.service.com", "AvailabilitySearchRequest");
        SearchFlightRequestB searchFlightRequest = new SearchFlightRequestB(searchRequest);
        JAXBElement<SearchFlightRequestB> jaxbElement =
                new JAXBElement<>(qName, SearchFlightRequestB.class, searchFlightRequest);

        String rawResponse = sendSoapRequest(ENDPOINT, SOAP_ACTION, jaxbElement, PROVIDER);

        LogDTO logDTOResponse = new LogDTO("response", rawResponse, PROVIDER);
        logService.saveLog(logDTOResponse);

        return parseFlightsFromXml(rawResponse, PARSER_CONFIG);
    }
}
