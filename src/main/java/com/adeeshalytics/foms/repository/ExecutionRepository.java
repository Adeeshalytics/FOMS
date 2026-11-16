package com.adeeshalytics.foms.repository;

import com.adeeshalytics.foms.domain.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExecutionRepository extends JpaRepository<Execution, UUID> {
    Page<Execution> findByBuyOrderIdOrSellOrderId(UUID buyOrderId, UUID sellOrderId, Pageable pageable);
}
