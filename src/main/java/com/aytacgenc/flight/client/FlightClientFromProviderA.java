package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.dto.SearchFlightRequestA;
import com.providerB.consumingwebservice.wsdl.Flight;
import com.providerA.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.JAXBElement;
import org.springframework.stereotype.Component;
import javax.xml.namespace.QName;
import java.util.List;

@Component
public class FlightClientFromProviderA extends BaseFlightClient {

    private static final String ENDPOINT = "http://localhost:8082/ws";
    private static final String SOAP_ACTION = "http://flightprovidera.service.com/AvailabilitySearchRequest";
    private static final String PROVIDER = "http://flightprovidera.service.com";
    private static final FlightParserConfig PARSER_CONFIG =
            new FlightParserConfig("flightNo", "origin", "destination");

    public List<Flight> getFlightsFromProviderA(SearchRequest searchRequest) throws Exception {
        QName qName = new QName("http://flightprovidera.service.com", "AvailabilitySearchRequest");
        SearchFlightRequestA searchFlightRequest = new SearchFlightRequestA(searchRequest);
        JAXBElement<SearchFlightRequestA> jaxbElement =
                new JAXBElement<>(qName, SearchFlightRequestA.class, searchFlightRequest);

        String rawResponse = sendSoapRequest(ENDPOINT, SOAP_ACTION, jaxbElement, PROVIDER);

        LogDTO logDTOResponse = new LogDTO("response", rawResponse, PROVIDER);
        logService.saveLog(logDTOResponse);

        return parseFlightsFromXml(rawResponse, PARSER_CONFIG);
    }
}
