package com.aytacgenc.flight.controller;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.dto.SearchFlightResponse;
import com.aytacgenc.flight.service.FlightAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FlightController {

    private final FlightAggregationService flightService;

    @GetMapping("/search-cheap/{departure}/{arrival}/{departureDate}/{flightNo}")
    public ResponseEntity<SearchFlightResponse> searchFlightsCheapest(@PathVariable String departure,
                                                                      @PathVariable String arrival,
                                                                      @PathVariable LocalDateTime departureDate,
                                                                      @PathVariable String flightNo) {
        List<FlightDTO> flights = flightService.searchFlightsCheapest(new SearchFlightRequest(flightNo, departure, arrival, departureDate));
        SearchFlightResponse response = new SearchFlightResponse(flights);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{departure}/{arrival}/{departureDate}/{flightNo}")
    public ResponseEntity<SearchFlightResponse> searchFlights(@PathVariable String departure,
                                                              @PathVariable String arrival,
                                                              @PathVariable LocalDateTime departureDate,
                                                              @PathVariable String flightNo) {
        List<FlightDTO> flights = flightService.searchFlightsNormal(new SearchFlightRequest(flightNo, departure, arrival, departureDate));
        SearchFlightResponse response = new SearchFlightResponse(flights);
        return ResponseEntity.ok(response);
    }
}
