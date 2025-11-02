package com.aytacgenc.flight.service;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.repository.LogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogService {
    private final LogRepository repository;

    public LogDTO saveLog(LogDTO log) {
        return repository.save(log);
    }
}
