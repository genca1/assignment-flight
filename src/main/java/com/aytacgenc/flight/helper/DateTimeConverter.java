package com.aytacgenc.flight.helper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;

public class DateTimeConverter {

    private static final DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to initialize DatatypeFactory", e);
        }
    }

    /**
     * Converts LocalDateTime to XMLGregorianCalendar for SOAP requests
     *
     * @param dateTime LocalDateTime to convert
     * @return XMLGregorianCalendar or null if input is null
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(
                    dateTime.atZone(ZoneId.from(ZoneOffset.UTC))
            );
            return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert LocalDateTime to XMLGregorianCalendar", e);
        }
    }
}