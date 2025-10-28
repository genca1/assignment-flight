package com.aytacgenc.flight.repository;

import com.aytacgenc.flight.dto.LogDTO;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<LogDTO, Long> {
}
