package nl.gerimedica.assignment.integration.rest;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics API", description = "Endpoints for viewing basic application metrics")
public class MetricsController {

    private final MeterRegistry meterRegistry;

    /**
     * Get a summary of hospital-related metrics counters
     */
    @GetMapping("/hospital")
    @Operation(summary = "Get hospital metrics summary")
    public ApiResponse<Map<String, Double>> getHospitalMetrics() {
        Map<String, Double> counters = meterRegistry.getMeters().stream()
                .filter(meter -> meter.getId().getName().startsWith("hospital") &&
                        meter.getId().getType() == Meter.Type.COUNTER)
                .collect(Collectors.toMap(
                        meter -> meter.getId().getName(),
                        meter -> meterRegistry.get(meter.getId().getName()).counter().count()
                ));

        return ApiResponse.success("Hospital metrics retrieved successfully", counters);
    }
}
