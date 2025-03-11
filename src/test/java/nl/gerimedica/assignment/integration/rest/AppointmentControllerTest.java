package nl.gerimedica.assignment.integration.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    private static final String SSN = "123-22-4567";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        // Clean up database
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();

        // Set up a test patient with appointment
        Patient patient = new Patient("Simple Test Patient", SSN);
        Appointment appointment = new Appointment("Simple Test Reason",
                LocalDateTime.now().plusDays(7), patient);
        patient.addAppointment(appointment);
        patientRepository.save(patient);
    }

    @Test
    void getPatient_shouldReturnPatient() throws Exception {
        mockMvc.perform(get("/api/hospital/patients/123-22-4567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Simple Test Patient"))
                .andExpect(jsonPath("$.data.ssn").value(SSN));
    }

    @Test
    void getAppointmentsByReason_shouldReturnMatchingAppointments() throws Exception {
        mockMvc.perform(get("/api/hospital/appointments/reason/exact")
                        .param("reason", "Simple Test Reason"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].reason").value("Simple Test Reason"));
    }

    @Test
    void createBulkAppointments_shouldCreateAppointments() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("patientName", "MockMvc Test");
        request.put("ssn", SSN);
        request.put("reasons", Arrays.asList("Reason 1", "Reason 2"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        request.put("dates", Arrays.asList(
                LocalDateTime.now().plusDays(1).format(formatter),
                LocalDateTime.now().plusDays(2).format(formatter)
        ));

        // Act & Assert
        mockMvc.perform(post("/api/hospital/appointments/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].reason").value("Reason 1"))
                .andExpect(jsonPath("$.data[1].reason").value("Reason 2"))
                .andExpect(jsonPath("$.data[0].patient.name").value("Simple Test Patient"));
    }

    @Test
    void deleteAppointments_shouldDeleteAllAppointments() throws Exception {
        // Verify appointments exist before delete
        String ssn = SSN;

        mockMvc.perform(get("/api/hospital/appointments/latest/{ssn}", ssn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());

        // Delete appointments
        mockMvc.perform(delete("/api/hospital/appointments/patient/{ssn}", ssn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify appointments are deleted
        mockMvc.perform(get("/api/hospital/appointments/latest/{ssn}", ssn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void invalidRequest_shouldReturnValidationError() throws Exception {
        // Missing required fields
        Map<String, Object> request = Map.of(
                "patientName", "Invalid Test"
                // Missing ssn, reasons, dates
        );

        mockMvc.perform(post("/api/hospital/appointments/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getMetrics_shouldReturnMetricsData() throws Exception {
        mockMvc.perform(get("/api/metrics/hospital"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}