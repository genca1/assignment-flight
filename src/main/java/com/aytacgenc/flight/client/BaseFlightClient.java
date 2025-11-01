package com.aytacgenc.flight.client;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.service.LogService;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseFlightClient extends WebServiceGatewaySupport {

    @Autowired
    protected LogService logService;

    protected <T, R> R sendSoapRequest(String endpoint, String soapAction,
                                       JAXBElement<T> requestElement,
                                       Class<R> responseType,
                                       String provider) {
        return getWebServiceTemplate().sendAndReceive(
                endpoint,
                createRequestCallback(soapAction, requestElement, provider),
                new SimpleMessageExtractor<>(responseType)
        );
    }

    private class SimpleMessageExtractor<R> implements WebServiceMessageExtractor<R> {
        private final Class<R> responseType;

        public SimpleMessageExtractor(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        public R extractData(WebServiceMessage message) throws IOException, TransformerException {
            // Capture SOAP Response as XML string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            String soapResponseXml = baos.toString(StandardCharsets.UTF_8);

            // Save the raw XML
            logService.saveLog(new LogDTO("response", soapResponseXml, "someUrlOrProvider"));

            // Unmarshal the response normally
            Object response = getWebServiceTemplate().getUnmarshaller().unmarshal(message.getPayloadSource());
            if (response instanceof JAXBElement<?>) {
                return ((JAXBElement<R>) response).getValue();
            }
            return (R) response;
        }
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
                String soapRequestXml = baos.toString(StandardCharsets.UTF_8);

                LogDTO logRequest = new LogDTO("request", soapRequestXml, provider);
                logService.saveLog(logRequest);
            } catch (Exception e) {
                throw new RuntimeException("Failed to log SOAP request", e);
            }
        };
    }
}