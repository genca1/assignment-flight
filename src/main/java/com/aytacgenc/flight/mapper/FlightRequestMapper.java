package com.aytacgenc.flight.mapper;

import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.helper.DateTimeConverter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

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

    public com.providerB.consumingwebservice.wsdl.Flight mapFlight(com.providerA.consumingwebservice.wsdl.Flight flightA) {
        com.providerB.consumingwebservice.wsdl.Flight flightB = new com.providerB.consumingwebservice.wsdl.Flight();
        flightB.setFlightNumber(flightA.getFlightNumber());
        flightB.setDeparture(flightA.getDeparture());
        flightB.setArrival(flightA.getArrival());
        flightB.setDeparturedatetime(flightA.getDeparturedatetime());
        flightB.setArrivaldatetime(flightA.getArrivaldatetime());
        flightB.setPrice(flightA.getPrice());
        return flightB;
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
