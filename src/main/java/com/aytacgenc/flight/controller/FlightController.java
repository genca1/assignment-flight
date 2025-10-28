package com.aytacgenc.flight.controller;

import com.aytacgenc.flight.dto.SearchFlightRequest;
import com.aytacgenc.flight.dto.SearchFlightResponse;
import com.aytacgenc.flight.service.FlightAggregationService;
import com.providerB.consumingwebservice.wsdl.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    private FlightAggregationService flightService;

    @PostMapping("/searchNormal")
    public ResponseEntity<SearchFlightResponse> searchFlightsCheapest(@RequestBody SearchFlightRequest request) {

        logger.info("Received flight search request: {} to {} on {}",
                request.getDeparture(),
                request.getArrival(),
                request.getDepartureDate());

        try {
            List<Flight> flights = flightService.searchFlightsCheapest(request);

            SearchFlightResponse response = new SearchFlightResponse(flights);

            if (flights.isEmpty()) {
                response.setErrorMessage("No flights found for the given criteria");
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing flight search request", e);

            SearchFlightResponse errorResponse = new SearchFlightResponse();
            errorResponse.setErrorMessage("Error searching flights: " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<SearchFlightResponse> searchFlights(@RequestBody SearchFlightRequest request) {

        logger.info("Received flight search request: {} to {} on {}",
                request.getDeparture(),
                request.getArrival(),
                request.getDepartureDate());

        try {
            List<Flight> flights = flightService.searchFlightsNormal(request);

            SearchFlightResponse response = new SearchFlightResponse(flights);

            if (flights.isEmpty()) {
                response.setErrorMessage("No flights found for the given criteria");
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing flight search request", e);

            SearchFlightResponse errorResponse = new SearchFlightResponse();
            errorResponse.setErrorMessage("Error searching flights: " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Flight service is running");
    }
}
