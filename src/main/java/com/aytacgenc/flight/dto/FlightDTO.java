package com.aytacgenc.flight.dto;

import jakarta.xml.bind.annotation.XmlSchemaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FlightDTO {

    protected String flightNumber;
    protected String departure;
    protected String arrival;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar departuredatetime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arrivaldatetime;
    protected BigDecimal price;

    protected String provider;
}
