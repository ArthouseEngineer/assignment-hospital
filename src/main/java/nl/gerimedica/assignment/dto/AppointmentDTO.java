package nl.gerimedica.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentDTO(
        @NotBlank(message = "Reason is required") String reason,
        @NotNull(message = "Appointment date is required") LocalDateTime appointmentDate,
        PatientDTO patientDTO
) {}
