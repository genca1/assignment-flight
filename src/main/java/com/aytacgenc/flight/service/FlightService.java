package com.aytacgenc.flight.service;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightAggregationService flightAggregationService;

    public List<FlightDTO> searchFlightsNormal(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());
        List<FlightDTO> allFlights = flightAggregationService.getAllFlightsCombined(request);

        return allFlights.stream()
                .filter(flight -> pattern.matcher(flight.getFlightNumber()).find())
                .collect(Collectors.toList());
    }

    public List<FlightDTO> searchFlightsCheapest(SearchFlightRequest request) {
        Pattern pattern = Pattern.compile("^" + request.getFlightNo());
        Map<String, List<FlightDTO>> allFlightsMap = flightAggregationService.getAllFlights(request);

        Map<String, FlightDTO> cheapestFlights = new HashMap<>();

        allFlightsMap.forEach((provider, flights) -> {
            flights.stream()
                    .filter(flight -> pattern.matcher(flight.getFlightNumber()).find())
                    .forEach(flight -> {
                        String flightNo = flight.getFlightNumber();
                        FlightDTO current = cheapestFlights.get(flightNo);
                        if (current == null || flight.getPrice().compareTo(current.getPrice()) < 0) {
                            cheapestFlights.put(flightNo, flight);
                        }
                    });
        });

        return new ArrayList<>(cheapestFlights.values());
    }
}