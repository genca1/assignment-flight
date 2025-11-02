package com.aytacgenc.flight.mapper;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.helper.DateTimeConverter;
import com.providerA.consumingwebservice.wsdl.Flight;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightRequestMapper {

    public com.providerA.consumingwebservice.wsdl.SearchRequest toProviderARequest(SearchFlightRequest restRequest) {
        com.providerA.consumingwebservice.wsdl.SearchRequest soapRequest = new com.providerA.consumingwebservice.wsdl.SearchRequest();
        mapCommonFields(restRequest, soapRequest);
        return soapRequest;
    }

    public com.providerB.consumingwebservice.wsdl.SearchRequest toProviderBRequest(SearchFlightRequest restRequest) {
        com.providerB.consumingwebservice.wsdl.SearchRequest soapRequest = new com.providerB.consumingwebservice.wsdl.SearchRequest();
        mapCommonFields(restRequest, soapRequest);
        return soapRequest;
    }

    public List<FlightDTO> mapListFlightDTO (List<?> flightObjectList) {
        List<FlightDTO> flightDTOS = new ArrayList<>();
        for (Object f: flightObjectList) {
            flightDTOS.add(mapFlightDTO(f));
        }

        return flightDTOS;
    }

    public FlightDTO mapFlightDTO(Object flightObject) {
        FlightDTO flightDTO = new FlightDTO();
        if (flightObject instanceof Flight flight) {
            flightDTO.setFlightNumber(flight.getFlightNumber());
            flightDTO.setArrival(flight.getArrival());
            flightDTO.setDeparture(flight.getDeparture());
            flightDTO.setArrivaldatetime(flight.getArrivaldatetime());
            flightDTO.setDeparturedatetime(flight.getDeparturedatetime());
            flightDTO.setPrice(flight.getPrice());
            flightDTO.setProvider("ProviderA");
        } else if (flightObject instanceof com.providerB.consumingwebservice.wsdl.Flight flight) {
            flightDTO.setFlightNumber(flight.getFlightNumber());
            flightDTO.setArrival(flight.getArrival());
            flightDTO.setDeparture(flight.getDeparture());
            flightDTO.setArrivaldatetime(flight.getArrivaldatetime());
            flightDTO.setDeparturedatetime(flight.getDeparturedatetime());
            flightDTO.setPrice(flight.getPrice());
            flightDTO.setProvider("ProviderB");
        }
        return flightDTO;
    }


    private void mapCommonFields(SearchFlightRequest source, Object target) {
        // Convert date once
        XMLGregorianCalendar xmlDate = DateTimeConverter.toXMLGregorianCalendar(source.getDepartureDate());

        if (target instanceof com.providerA.consumingwebservice.wsdl.SearchRequest) {
            com.providerA.consumingwebservice.wsdl.SearchRequest requestA = (com.providerA.consumingwebservice.wsdl.SearchRequest) target;
            requestA.setDeparture(source.getDeparture());
            requestA.setArrival(source.getArrival());
            requestA.setDepartureDate(xmlDate);
        } else if (target instanceof com.providerB.consumingwebservice.wsdl.SearchRequest) {
            com.providerB.consumingwebservice.wsdl.SearchRequest requestB = (com.providerB.consumingwebservice.wsdl.SearchRequest) target;
            requestB.setDeparture(source.getDeparture());
            requestB.setArrival(source.getArrival());
            requestB.setDepartureDate(xmlDate);
        }
    }
}
