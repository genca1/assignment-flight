package com.aytacgenc.flight.service;

import com.aytacgenc.flight.client.FlightClientFromProviderA;
import com.aytacgenc.flight.client.FlightClientFromProviderB;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.mapper.FlightRequestMapper;
import com.providerB.consumingwebservice.wsdl.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FlightAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(FlightAggregationService.class);

    @Autowired
    private FlightClientFromProviderA providerAClient;

    @Autowired
    private FlightClientFromProviderB providerBClient;

    @Autowired
    private FlightRequestMapper flightRequestMapper;

    public List<Flight> searchFlightsNormal(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());
        List<Flight> allFlights = getAllFlightsCombined(request);

        return allFlights.stream()
                .filter(flight -> pattern.matcher(flight.getFlightNumber()).find())
                .collect(Collectors.toList());
    }

    public List<Flight> searchFlightsCheapest(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());
        Map<String, List<Flight>> allFlightsMap = getAllFlights(request);

        Map<String, Flight> cheapestFlights = new HashMap<>();

        allFlightsMap.forEach((provider, flights) -> {
            flights.stream()
                    .filter(flight -> pattern.matcher(flight.getFlightNumber()).find())
                    .forEach(flight -> {
                        String flightNo = flight.getFlightNumber();
                        Flight current = cheapestFlights.get(flightNo);
                        if (current == null || flight.getPrice().compareTo(current.getPrice()) < 0) {
                            cheapestFlights.put(flightNo, flight);
                        }
                    });
        });

        return new ArrayList<>(cheapestFlights.values());
    }

    public List<Flight> getAllFlightsCombined(SearchFlightRequest request) {
        Map<String, List<Flight>> allFlightsMap = getAllFlights(request);
        return allFlightsMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<String, List<Flight>> getAllFlights(SearchFlightRequest request) {
        logger.info("Searching flights from {} to {} on {}",
                request.getDeparture(),
                request.getArrival(),
                request.getDepartureDate());

        // Convert REST request to SOAP request format
        com.providerA.consumingwebservice.wsdl.SearchRequest requestA = flightRequestMapper.toProviderARequest(request);
        com.providerB.consumingwebservice.wsdl.SearchRequest requestB = flightRequestMapper.toProviderBRequest(request);

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
}
