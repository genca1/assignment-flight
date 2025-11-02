package com.aytacgenc.flight.service;

import com.aytacgenc.flight.client.FlightClientFromProviderA;
import com.aytacgenc.flight.client.FlightClientFromProviderB;
import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.mapper.FlightRequestMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(FlightAggregationService.class);
    private final FlightClientFromProviderA providerAClient;
    private final FlightClientFromProviderB providerBClient;
    private final FlightRequestMapper flightRequestMapper;

    public List<FlightDTO> getAllFlightsCombined(SearchFlightRequest request) {
        Map<String, List<FlightDTO>> allFlightsMap = getAllFlights(request);
        return allFlightsMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<String, List<FlightDTO>> getAllFlights(SearchFlightRequest request) {
        // Convert REST request to SOAP request format
        com.providerA.consumingwebservice.wsdl.SearchRequest requestA = flightRequestMapper.toProviderARequest(request);
        com.providerB.consumingwebservice.wsdl.SearchRequest requestB = flightRequestMapper.toProviderBRequest(request);

        CompletableFuture<List<FlightDTO>> futureA = CompletableFuture.supplyAsync(() -> {
            List<FlightDTO> flightsA = new ArrayList<>();
            try {
                flightsA = providerAClient.getFlightsFromProviderA(requestA);
            } catch (Exception e) {
                logger.error("Error aggregating flight results from providerA", e);
            }
            return flightsA;
        });

        CompletableFuture<List<FlightDTO>> futureB = CompletableFuture.supplyAsync(() -> {
            List<FlightDTO> flightsB = new ArrayList<>();
            try {
                flightsB = providerBClient.getFlightsFromProviderB(requestB);
            } catch (Exception e) {
                logger.error("Error aggregating flight results from providerB", e);
            }
            return flightsB;
        });

        try {
            Map<String, List<FlightDTO>> allFlights = new HashMap<>();
            allFlights.put("providerA", futureA.get());
            allFlights.put("providerB", futureB.get());
            return allFlights;
        } catch (Exception e) {
            logger.error("Error aggregating flight results", e);
            return new HashMap<>();
        }
    }
}
