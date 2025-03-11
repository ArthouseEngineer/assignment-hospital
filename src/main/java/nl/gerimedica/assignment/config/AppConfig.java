package nl.gerimedica.assignment.config;


import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Application Configuration for metrics and monitoring
 * <p>
 * Sets up Micrometer with Prometheus for basic metrics collection
 * and customizes the meter registry with application information.
 */
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    /**
     * Main Prometheus meter registry for collecting metrics
     */
    @Bean
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("application", "hospital-management");
        return registry;
    }

    /**
     * Customizes the meter registry with common tags
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
            @Value("${spring.application.name:hospital-app}") String applicationName) {
        return registry -> registry.config()
                .commonTags("application", applicationName);
    }

    /**
     * Creates a CountedAspect for processing @Counted annotations
     * This allows automatic counting of method invocations annotated with @Counted
     */
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }
}
