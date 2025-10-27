package com.aytacgenc.flight.service;

import com.aytacgenc.flight.client.FlightClientFromProviderA;
import com.aytacgenc.flight.client.FlightClientFromProviderB;
import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.helper.DateTimeConverter;
import com.providerA.consumingwebservice.wsdl.SearchRequest;
import com.providerB.consumingwebservice.wsdl.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class FlightAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(FlightAggregationService.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private FlightClientFromProviderA providerAClient;

    @Autowired
    private FlightClientFromProviderB providerBClient;

    public List<Flight> searchFlights(SearchFlightRequest request) {
        logger.info("Searching flights from {} to {} on {}",
                request.getDeparture(),
                request.getArrival(),
                request.getDepartureDate());

        // Convert REST request to SOAP request format
        String departureDateTimeStr = formatDateTime(request.getDepartureDate());

        // Create requests for both providers
        SearchRequest requestA = new SearchRequest();
        requestA.setDeparture(request.getDeparture());
        requestA.setArrival(request.getArrival());

        XMLGregorianCalendar xmlGregorianCalendar =
                DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(departureDateTimeStr));

        requestA.setDepartureDate(xmlGregorianCalendar);

        com.providerB.consumingwebservice.wsdl.SearchRequest requestB =
                new com.providerB.consumingwebservice.wsdl.SearchRequest();
        requestB.setDeparture(request.getDeparture());
        requestB.setArrival(request.getArrival());

        XMLGregorianCalendar xmlGregorianCalendarB =
                DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(departureDateTimeStr));

        requestB.setDepartureDate(xmlGregorianCalendarB);

        // Call both providers in parallel
        CompletableFuture<List<Flight>> futureA = CompletableFuture.supplyAsync(() -> {
            List<Flight> flightsA = null;
            try {
                flightsA = providerAClient.getFlightsFromProviderA(requestA);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return flightsA;
        });

        CompletableFuture<List<Flight>> futureB = CompletableFuture.supplyAsync(() -> {
            List<Flight> flightsB = null;
            try {
                flightsB = providerBClient.getFlightsFromProviderB(requestB);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return flightsB;
        });

        // Wait for both to complete and combine results
        try {
            List<Flight> allFlights = new ArrayList<>();
            allFlights.addAll(futureA.get());
            allFlights.addAll(futureB.get());

            logger.info("Total flights found: {}", allFlights.size());

            // Sort by price
            return allFlights.stream()
                    .sorted((f1, f2) -> f1.getPrice().compareTo(f2.getPrice()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error aggregating flight results", e);
            return new ArrayList<>();
        }
    }

    private List<FlightDTO> convertProviderAFlights(
            List<Flight> flights) {
        return flights.stream()
                .map(f -> {
                    FlightDTO dto = new FlightDTO();
                    dto.setFlightNumber(f.getFlightNumber());
                    dto.setDeparture(f.getDeparture());
                    dto.setArrival(f.getArrival());
                    if (f.getDeparturedatetime() != null) {
                        dto.setDepartureDateTime(f.getDeparturedatetime().toString());
                    }
                    if (f.getArrivaldatetime() != null) {
                        dto.setArrivalDateTime(f.getArrivaldatetime().toString());
                    }
                    dto.setPrice(f.getPrice());
                    dto.setProvider("Provider A");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<FlightDTO> convertProviderBFlights(
            List<Flight> flights) {
        return flights.stream()
                .map(f -> {
                    FlightDTO dto = new FlightDTO();
                    dto.setFlightNumber(f.getFlightNumber());
                    dto.setDeparture(f.getDeparture());
                    dto.setArrival(f.getArrival());
                    if (f.getDeparturedatetime() != null) {
                        dto.setDepartureDateTime(f.getDeparturedatetime().toString());
                    }
                    if (f.getArrivaldatetime() != null) {
                        dto.setArrivalDateTime(f.getArrivaldatetime().toString());
                    }
                    dto.setPrice(f.getPrice());
                    dto.setProvider("Provider B");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
}
