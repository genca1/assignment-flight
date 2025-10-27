package com.aytacgenc.flight.client;

import com.aytacgenc.flight.config.SoapClientConfig;
import com.aytacgenc.flight.dto.SearchFlightRequestB;
import com.aytacgenc.flight.helper.DateTimeConverter;
import com.providerB.consumingwebservice.wsdl.Flight;
import com.providerB.consumingwebservice.wsdl.SearchRequest;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FlightClientFromProviderB extends WebServiceGatewaySupport {

    @Autowired
    private SoapClientConfig config;

    public List<Flight> getFlightsFromProviderB(SearchRequest searchRequest) throws Exception {
        QName qName = new QName("http://flightproviderb.service.com", "AvailabilitySearchRequest");
        SearchFlightRequestB searchFlightRequest = new SearchFlightRequestB(searchRequest);
        JAXBElement<SearchFlightRequestB> jaxbElement = new JAXBElement<>(qName, SearchFlightRequestB.class, searchFlightRequest);

        // Get raw XML response
        String rawResponse = getWebServiceTemplate().sendAndReceive(
                "http://localhost:8083/ws",
                new WebServiceMessageCallback() {
                    @Override
                    public void doWithMessage(WebServiceMessage message) throws IOException {
                        ((SoapMessage) message).setSoapAction("http://flightproviderb.service.com/AvailabilitySearchRequest");
                        getWebServiceTemplate().getMarshaller().marshal(jaxbElement, message.getPayloadResult());
                    }
                },
                new WebServiceMessageExtractor<String>() {
                    @Override
                    public String extractData(WebServiceMessage message) throws IOException, TransformerException {
                        StringWriter writer = new StringWriter();
                        TransformerFactory.newInstance().newTransformer().transform(
                                message.getPayloadSource(),
                                new StreamResult(writer)
                        );
                        return writer.toString();
                    }
                }
        );

        System.out.println("Raw SOAP response: " + rawResponse);

        // Parse the response
        return parseFlightsFromXml(rawResponse);
    }

    private List<Flight> parseFlightsFromXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        List<Flight> flights = new ArrayList<>();

        NodeList flightNodes = doc.getElementsByTagName("flight");

        System.out.println("Found " + flightNodes.getLength() + " flight nodes");

        for (int i = 0; i < flightNodes.getLength(); i++) {
            Element flightElement = (Element) flightNodes.item(i);
            Flight flight = new Flight();

            String flightNumber = getChildElementText(flightElement, "flightNumber");
            String departure = getChildElementText(flightElement, "departure");
            String arrival = getChildElementText(flightElement, "arrival");

            // These already matched
            String departureDateTime = getChildElementText(flightElement, "departuredatetime");
            String arrivalDateTime = getChildElementText(flightElement, "arrivaldatetime");
            String priceText = getChildElementText(flightElement, "price");

            flight.setFlightNumber(flightNumber);
            flight.setDeparture(departure);
            flight.setArrival(arrival);

            flight.setFlightNumber(flightNumber);
            flight.setDeparture(departure);
            flight.setArrival(arrival);
            if (departureDateTime != null && !departureDateTime.isBlank()) {
                XMLGregorianCalendar gregDeparture = DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(departureDateTime));
                flight.setDeparturedatetime(gregDeparture);
            }
            if (arrivalDateTime != null && !arrivalDateTime.isBlank()) {
                XMLGregorianCalendar gregArrival = DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(arrivalDateTime));
                flight.setArrivaldatetime(gregArrival);
            }

            if (priceText != null && !priceText.isEmpty()) {
                try {
                          flight.setPrice(BigDecimal.valueOf(Double.parseDouble(priceText)));
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse price: " + priceText);
                }
            }

            flights.add(flight);
            System.out.println("Parsed flight: " + flightNumber + " from " + departure + " to " + arrival + " - $" + priceText);
        }

        return flights;
    }

    private String getChildElementText(Element parent, String tagName) {
        NodeList children = parent.getElementsByTagName(tagName);
        if (children.getLength() > 0) {
            String text = children.item(0).getTextContent();
            return text != null ? text.trim() : null;
        }
        return null;
    }
}