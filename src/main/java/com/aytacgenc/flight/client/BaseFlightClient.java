package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.helper.DateTimeConverter;
import com.aytacgenc.flight.service.LogService;
import com.providerB.consumingwebservice.wsdl.Flight;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFlightClient extends WebServiceGatewaySupport {

    @Autowired
    protected LogService logService;

    protected <T> String sendSoapRequest(String endpoint, String soapAction,
                                         JAXBElement<T> requestElement, String provider) throws Exception {
        return getWebServiceTemplate().sendAndReceive(
                endpoint,
                createRequestCallback(soapAction, requestElement, provider),
                createResponseExtractor()
        );
    }

    private <T> WebServiceMessageCallback createRequestCallback(String soapAction,
                                                                JAXBElement<T> requestElement,
                                                                String provider) {
        return message -> {
            ((SoapMessage) message).setSoapAction(soapAction);
            getWebServiceTemplate().getMarshaller().marshal(requestElement, message.getPayloadResult());

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                message.writeTo(baos);
                baos.flush();
                String soapRequestXml = baos.toString(StandardCharsets.UTF_8.name());

                LogDTO logRequest = new LogDTO("request", soapRequestXml, provider);
                logService.saveLog(logRequest);
            } catch (Exception e) {
                throw new RuntimeException("Failed to log SOAP request", e);
            }
        };
    }

    private WebServiceMessageExtractor<String> createResponseExtractor() {
        return message -> {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(
                    message.getPayloadSource(),
                    new StreamResult(writer)
            );
            return writer.toString();
        };
    }

    protected List<Flight> parseFlightsFromXml(String xml, FlightParserConfig parserConfig) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        List<Flight> flights = new ArrayList<>();
        NodeList flightNodes = doc.getElementsByTagName("flight");

        System.out.println("Found " + flightNodes.getLength() + " flight nodes");

        for (int i = 0; i < flightNodes.getLength(); i++) {
            Element flightElement = (Element) flightNodes.item(i);
            Flight flight = createFlightFromElement(flightElement, parserConfig);
            if (flight != null) {
                flights.add(flight);
            }
        }

        return flights;
    }

    private Flight createFlightFromElement(Element flightElement, FlightParserConfig config) {
        try {
            Flight flight = new Flight();

            String flightNumber = getChildElementText(flightElement, config.getFlightNumberTag());
            String departure = getChildElementText(flightElement, config.getDepartureTag());
            String arrival = getChildElementText(flightElement, config.getArrivalTag());
            String departureDateTime = getChildElementText(flightElement, "departuredatetime");
            String arrivalDateTime = getChildElementText(flightElement, "arrivaldatetime");
            String priceText = getChildElementText(flightElement, "price");

            flight.setFlightNumber(flightNumber);
            flight.setDeparture(departure);
            flight.setArrival(arrival);

            setFlightDateTime(flight, departureDateTime, arrivalDateTime);
            setFlightPrice(flight, priceText);

            System.out.println("Parsed flight: " + flightNumber + " from " + departure + " to " + arrival + " - $" + priceText);
            return flight;
        } catch (Exception e) {
            System.err.println("Failed to parse flight element: " + e.getMessage());
            return null;
        }
    }

    private void setFlightDateTime(Flight flight, String departureDateTime, String arrivalDateTime) {
        if (departureDateTime != null && !departureDateTime.isBlank()) {
            XMLGregorianCalendar gregDeparture = DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(departureDateTime));
            flight.setDeparturedatetime(gregDeparture);
        }
        if (arrivalDateTime != null && !arrivalDateTime.isBlank()) {
            XMLGregorianCalendar gregArrival = DateTimeConverter.toXMLGregorianCalendar(LocalDateTime.parse(arrivalDateTime));
            flight.setArrivaldatetime(gregArrival);
        }
    }

    private void setFlightPrice(Flight flight, String priceText) {
        if (priceText != null && !priceText.isEmpty()) {
            try {
                flight.setPrice(BigDecimal.valueOf(Double.parseDouble(priceText)));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse price: " + priceText);
            }
        }
    }

    private String getChildElementText(Element parent, String tagName) {
        NodeList children = parent.getElementsByTagName(tagName);
        if (children.getLength() > 0) {
            String text = children.item(0).getTextContent();
            return text != null ? text.trim() : null;
        }
        return null;
    }

    // Configuration for XML parsing
    protected static class FlightParserConfig {
        private final String flightNumberTag;
        private final String departureTag;
        private final String arrivalTag;

        public FlightParserConfig(String flightNumberTag, String departureTag, String arrivalTag) {
            this.flightNumberTag = flightNumberTag;
            this.departureTag = departureTag;
            this.arrivalTag = arrivalTag;
        }

        public String getFlightNumberTag() { return flightNumberTag; }
        public String getDepartureTag() { return departureTag; }
        public String getArrivalTag() { return arrivalTag; }
    }
}

