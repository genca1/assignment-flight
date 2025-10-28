package com.aytacgenc.flight.dto;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LogDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime logTime;
    private String type;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;

    private String url;

    public LogDTO() {}

    public LogDTO(String type, String body, String url) {
        this.logTime = LocalDateTime.now();
        this.type = type;
        this.body = body;
        this.url = url;
    }
    // Getters
    public Long getId() {
        return id;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public String getUrl() { return this.url;  }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUrl(String url) { this.url = url; }
}

