package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Execution;
import com.adeeshalytics.foms.repository.ExecutionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ExecutionService {
    private final ExecutionRepository executions;
    public ExecutionService(ExecutionRepository executions) { this.executions = executions; }
    @Transactional(readOnly = true) public Execution find(UUID id) { return executions.findById(id).orElseThrow(() -> new ResourceNotFoundException("execution not found")); }
    @Transactional(readOnly = true) public Page<Execution> list(Pageable pageable) { return executions.findAll(pageable); }
    @Transactional(readOnly = true) public Page<Execution> forOrder(UUID orderId, Pageable pageable) { return executions.findByBuyOrderIdOrSellOrderId(orderId, orderId, pageable); }
}
