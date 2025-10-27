package com.aytacgenc.flight.config;

import com.aytacgenc.flight.client.FlightClientFromProviderA;
import com.aytacgenc.flight.client.FlightClientFromProviderB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SoapClientConfig {

    @Bean(name = "providerAMarshaller")
    public Jaxb2Marshaller providerAMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setClassesToBeBound(
                com.aytacgenc.flight.dto.SearchFlightRequestA.class,
                com.providerA.consumingwebservice.wsdl.SearchRequest.class, // Add root types from generated code
                com.providerA.consumingwebservice.wsdl.ObjectFactory.class  // Often necessary for generated code
        );
        return marshaller;
    }

    @Bean(name = "providerBMarshaller")
    public Jaxb2Marshaller providerBMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                com.aytacgenc.flight.dto.SearchFlightRequestB.class,
                com.providerB.consumingwebservice.wsdl.SearchRequest.class, // Add root types from generated code
                com.providerB.consumingwebservice.wsdl.ObjectFactory.class  // Often necessary for generated code
        );
        return marshaller;
    }

    @Bean
    public FlightClientFromProviderA providerAClient() {
        FlightClientFromProviderA client = new FlightClientFromProviderA();
        client.setDefaultUri("http://localhost:8082/ws");
        client.setMarshaller(providerAMarshaller());
        client.setUnmarshaller(providerAMarshaller());
        return client;
    }

    @Bean
    public FlightClientFromProviderB providerBClient() {
        FlightClientFromProviderB client = new FlightClientFromProviderB();
        client.setDefaultUri("http://localhost:8083/ws");
        client.setMarshaller(providerBMarshaller());
        client.setUnmarshaller(providerBMarshaller());
        return client;
    }
}
