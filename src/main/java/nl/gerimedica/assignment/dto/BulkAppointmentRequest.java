package nl.gerimedica.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkAppointmentRequest(
        @NotBlank(message = "Patient name is required") String patientName,
        @NotBlank(message = "SSN is required") String ssn,
        @NotEmpty(message = "At least one reason is required") List<String> reasons,
        @NotEmpty(message = "At least one date is required") List<String> dates
) {}
