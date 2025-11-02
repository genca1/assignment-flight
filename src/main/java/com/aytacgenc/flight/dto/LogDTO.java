package com.aytacgenc.flight.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LogDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime logTime;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String url;

    public LogDTO(String response, String soapResponseXml, String someUrlOrProvider) {
        this.type = response;
        this.body = soapResponseXml;
        this.url = someUrlOrProvider;
    }
}

