package nl.gerimedica.assignment.dto;

import jakarta.validation.constraints.NotBlank;

public record PatientDTO(
        @NotBlank(message = "Patient name is required") String name,
        @NotBlank(message = "SSN is required") String ssn
) {
}
