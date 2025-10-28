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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FlightAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(FlightAggregationService.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private FlightClientFromProviderA providerAClient;

    @Autowired
    private FlightClientFromProviderB providerBClient;

    public List<Flight> searchFlightsNormal(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());

        Map<String, List<Flight>> allFlightsMap = getAllFlights(request);
        List<Flight> allFlights = new ArrayList<>();

        for (Map.Entry<String, List<Flight>> entry : allFlightsMap.entrySet()) {
            allFlights.addAll(entry.getValue());
        }

        List<Flight> searchedFlights = new ArrayList<>();
        for (Flight f : allFlights) {
            Matcher matcher = pattern.matcher(f.getFlightNumber());
            while (matcher.find()) {
                searchedFlights.add(f);
            }
        }
        return searchedFlights;
    }

    public List<Flight> searchFlightsCheapest(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());

        Map<String, List<Flight>> allFlightsMap = getAllFlights(request);

        Map<String, List<Flight>> necessaryFlights = new HashMap<>();

        for (Map.Entry<String, List<Flight>> entry : allFlightsMap.entrySet()) {
            for (Flight flight : entry.getValue()) {
                String flightNo = flight.getFlightNumber();
                Matcher matcher = pattern.matcher(flightNo);
                while (matcher.find()) {
                    necessaryFlights.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                    necessaryFlights.get(entry.getKey()).add(flight);
                }
            }
        }
        return findCheapestFlights(necessaryFlights);
    }

    public List<Flight> findCheapestFlights(Map<String, List<Flight>> allFlightsMap) {
        List<Flight> cheapestFlights = new ArrayList<>();
        Map<String, Flight> cheapestByFlightNo = new HashMap<>();

        for (Map.Entry<String, List<Flight>> entry : allFlightsMap.entrySet()) {
            for (Flight flight : entry.getValue()) {
                String flightNo = flight.getFlightNumber();
                Flight current = cheapestByFlightNo.get(flightNo);
                if (current == null || flight.getPrice().compareTo(current.getPrice()) < 0) {
                    cheapestByFlightNo.put(flightNo, flight);
                }
            }
        }

        cheapestFlights.addAll(cheapestByFlightNo.values());
        return cheapestFlights;
    }

    public Map<String, List<Flight>> getAllFlights(SearchFlightRequest request) {
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
            Map<String, List<Flight>> allFlights = new HashMap<>();
            allFlights.put("providerA", futureA.get());
            allFlights.put("providerB", futureB.get());
            return allFlights;
        } catch (Exception e) {
            logger.error("Error aggregating flight results", e);
            return new HashMap<>();
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
}
