package nl.gerimedica.assignment.integration.rest;

import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.dto.ApiResponse;
import nl.gerimedica.assignment.dto.AppointmentDTO;
import nl.gerimedica.assignment.dto.BulkAppointmentRequest;
import nl.gerimedica.assignment.dto.PatientDTO;
import nl.gerimedica.assignment.mappers.PatientMapper;
import nl.gerimedica.assignment.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.gerimedica.assignment.entity.Patient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
@Tag(name = "Hospital API", description = "Endpoints for managing patients and appointments")
public class AppointmentController {

    private final HospitalService hospitalService;
    private final PatientMapper patientMapper;

    @PostMapping("/appointments/bulk")
    @Operation(summary = "Create multiple appointments for a patient")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> createBulkAppointments(
            @Valid @RequestBody BulkAppointmentRequest request) {

        var appointments = hospitalService.bulkCreateAppointments(
                request.patientName(),
                request.ssn(),
                request.reasons(),
                request.dates()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointments created successfully", appointments));
    }

    @GetMapping("/patients/{ssn}")
    @Operation(summary = "Find a patient by SSN")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientBySSN(@PathVariable String ssn) {
        Patient patient = hospitalService.findPatientBySSN(ssn);
        return ResponseEntity.ok(ApiResponse.success(patientMapper.toDto(patient)));
    }

    @GetMapping("/appointments/reason/exact")
    @Operation(summary = "Get appointments by exact reason match")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByExactReason(
            @RequestParam String reason) {
        List<AppointmentDTO> appointments = hospitalService.getAppointmentsByReason(reason);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    @GetMapping("/appointments/reason/contains")
    @Operation(summary = "Get appointments containing reason keyword")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsContainingReason(
            @RequestParam String keyword) {
        List<AppointmentDTO> appointments = hospitalService.getAppointmentsContainingReason(keyword);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    @DeleteMapping("/appointments/patient/{ssn}")
    @Operation(summary = "Delete all appointments for a patient")
    public ResponseEntity<ApiResponse<Void>> deleteAppointmentsBySSN(@PathVariable String ssn) {
        hospitalService.deleteAppointmentsBySSN(ssn);
        return ResponseEntity.ok(ApiResponse.success("Appointments deleted successfully", null));
    }

    @GetMapping("/appointments/latest/{ssn}")
    @Operation(summary = "Get the latest appointment for a patient")
    public ResponseEntity<ApiResponse<AppointmentDTO>> getLatestAppointment(@PathVariable String ssn) {
        AppointmentDTO appointment = hospitalService.findLatestAppointmentBySSN(ssn);
        if (appointment == null) {
            return ResponseEntity.ok(ApiResponse.success("No appointments found for patient", null));
        }
        return ResponseEntity.ok(ApiResponse.success(appointment));
    }
}
