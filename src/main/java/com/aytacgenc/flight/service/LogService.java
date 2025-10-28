package com.aytacgenc.flight.service;

import com.aytacgenc.flight.dto.LogDTO;
import com.aytacgenc.flight.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    LogRepository repository;

    public LogDTO saveLog(LogDTO log) {
        return repository.save(log);
    }
}
