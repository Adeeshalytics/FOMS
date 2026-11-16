package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.ExecutionService;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {
    private final ExecutionService executions;
    public ExecutionController(ExecutionService executions) { this.executions = executions; }
    @GetMapping public Page<ExecutionResponse> list(@PageableDefault(size = 50, sort = "executedAt", direction = Sort.Direction.DESC) Pageable pageable) { return executions.list(pageable).map(ExecutionResponse::from); }
    @GetMapping("/{executionId}") public ExecutionResponse get(@PathVariable UUID executionId) { return ExecutionResponse.from(executions.find(executionId)); }
}
