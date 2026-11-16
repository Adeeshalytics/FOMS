package com.adeeshalytics.foms.repository;

import com.adeeshalytics.foms.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {
    Optional<Instrument> findBySymbol(String symbol);
}
