package nl.gerimedica.assignment.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Simplified metrics service that focuses only on essential counters
 * without complex timer measurements.
 */
@Service
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Basic operation counters
    private final Counter appointmentsCreatedCounter;
    private final Counter appointmentsQueriedCounter;
    private final Counter appointmentsDeletedCounter;

    /**
     * Initialize with registry and create basic counters
     */
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Initialize counters
        this.appointmentsCreatedCounter = Counter.builder("hospital.appointments.created")
                .description("Number of appointments created")
                .register(meterRegistry);

        this.appointmentsQueriedCounter = Counter.builder("hospital.appointments.queried")
                .description("Number of appointment queries")
                .register(meterRegistry);

        this.appointmentsDeletedCounter = Counter.builder("hospital.appointments.deleted")
                .description("Number of appointments deleted")
                .register(meterRegistry);
    }

    /**
     * Record the creation of appointments
     * @param count Number of appointments created
     */
    public void recordAppointmentsCreated(int count) {
        appointmentsCreatedCounter.increment(count);
    }

    /**
     * Record a query operation
     * @param queryType Type of query performed (for tagging)
     */
    public void recordAppointmentQueried(String queryType) {
        appointmentsQueriedCounter.increment();
        meterRegistry.counter("hospital.appointments.query.count", "type", queryType).increment();
    }

    /**
     * Record deletion of appointments
     * @param count Number of appointments deleted
     */
    public void recordAppointmentsDeleted(int count) {
        appointmentsDeletedCounter.increment(count);
        meterRegistry.gauge("hospital.appointments.last_deletion_size", count);
    }
}
