package com.aytacgenc.flight.controller;

import com.aytacgenc.flight.dto.FlightDTO;
import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.dto.SearchFlightResponse;
import com.aytacgenc.flight.service.FlightAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FlightController {

    private final FlightAggregationService flightService;

    @PostMapping("/searchNormal")
    public ResponseEntity<SearchFlightResponse> searchFlightsCheapest(@RequestBody SearchFlightRequest request) {

        List<FlightDTO> flights = flightService.searchFlightsCheapest(request);
        SearchFlightResponse response = new SearchFlightResponse(flights);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchFlightResponse> searchFlights(@RequestBody SearchFlightRequest request) {
        List<FlightDTO> flights = flightService.searchFlightsNormal(request);
        SearchFlightResponse response = new SearchFlightResponse(flights);
        return ResponseEntity.ok(response);
    }
}
