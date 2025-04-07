package nl.gerimedica.assignment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gerimedica.assignment.dto.ApiResponse;
import nl.gerimedica.assignment.entity.Patient;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Simple integration test with minimal configuration
 * Uses an in-memory H2 database and TestRestTemplate
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FullIntegrationTest {

    private static final String SSN = "123-22-4567";

    @Autowired
    private TestRestTemplate restTemplate;

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

        // Create a test patient
        Patient patient = new Patient("Test Patient", SSN);
        patientRepository.save(patient);
    }

    @Test
    void getPatient_shouldReturnPatient() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/hospital/patients/123-22-4567", String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        try {
            ApiResponse<Map<String, Object>> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {
                    });

            assertTrue(apiResponse.success());
            assertNotNull(apiResponse.data());

            Map<String, Object> patientData = apiResponse.data();
            assertEquals("Test Patient", patientData.get("name"));
            assertEquals(SSN, patientData.get("ssn"));
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }

    @Test
    void getNonExistentPatient_shouldReturn404() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/hospital/patients/non-existent", String.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        try {
            ApiResponse<?> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {
                    });

            assertFalse(apiResponse.success());
            assertNull(apiResponse.data());
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }

    @Test
    void createAppointments_shouldCreateAppointmentsSuccessfully() {
        // Arrange
        String patientName = "New Patient";
        String ssn = "123-22-4567";

        Map<String, Object> request = new HashMap<>();
        request.put("patientName", patientName);
        request.put("ssn", ssn);
        request.put("reasons", Arrays.asList("Reason 1", "Reason 2"));

        // Current time plus 1 and 2 days in ISO format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        request.put("dates", Arrays.asList(
                LocalDateTime.now().plusDays(1).format(formatter),
                LocalDateTime.now().plusDays(2).format(formatter)
        ));

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/hospital/appointments/bulk", request, String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        try {
            ApiResponse<List<Map<String, Object>>> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {
                    });

            assertTrue(apiResponse.success());
            assertNotNull(apiResponse.data());
            assertEquals(2, apiResponse.data().size());

            // Verify patient was created
            assertTrue(patientRepository.existsBySsn(ssn));

            // Verify appointments were created
            assertEquals(2, appointmentRepository.findByPatientSsn(ssn).size());
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }

    @Test
    void deleteAppointments_shouldDeleteAllAppointments() {
        // Arrange - Create a patient with appointments

        Map<String, Object> request = new HashMap<>();
        request.put("patientName", "Delete Test");
        request.put("ssn", SSN);
        request.put("reasons", Arrays.asList("Delete Test 1", "Delete Test 2"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        request.put("dates", Arrays.asList(
                LocalDateTime.now().plusDays(1).format(formatter),
                LocalDateTime.now().plusDays(2).format(formatter)
        ));

        // Create appointments first
        restTemplate.postForEntity("/api/hospital/appointments/bulk", request, String.class);

        // Verify appointments exist
        assertFalse(appointmentRepository.findByPatientSsn(SSN).isEmpty());

        // Act
        restTemplate.delete("/api/hospital/appointments/patient/{ssn}", SSN);

        // Assert
        assertTrue(appointmentRepository.findByPatientSsn(SSN).isEmpty());
        assertTrue(patientRepository.existsBySsn(SSN)); // Patient should still exist
    }

    @Test
    void completeWorkflow_shouldWorkEndToEnd() {
        // Arrange
        String reason = "Workflow Test";

        // Step 1: Create patient with appointment
        Map<String, Object> request = new HashMap<>();
        request.put("patientName", "Workflow Test");
        request.put("ssn", SSN);
        request.put("reasons", List.of(reason));
        request.put("dates", List.of(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME)));

        // Act & Assert - Step 1: Create appointment
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                "/api/hospital/appointments/bulk", request, String.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Step 2: Get patient
        ResponseEntity<String> patientResponse = restTemplate.getForEntity(
                "/api/hospital/patients/{ssn}", String.class, SSN);
        assertEquals(HttpStatus.OK, patientResponse.getStatusCode());

        // Step 3: Get appointments by reason
        ResponseEntity<String> reasonResponse = restTemplate.getForEntity(
                "/api/hospital/appointments/reason/exact?reason={reason}", String.class, reason);
        assertEquals(HttpStatus.OK, reasonResponse.getStatusCode());

        try {
            ApiResponse<List<Map<String, Object>>> apiResponse = objectMapper.readValue(
                    reasonResponse.getBody(),
                    new TypeReference<>() {
                    });

            assertTrue(apiResponse.success());
            assertFalse(apiResponse.data().isEmpty());
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }

        // Step 4: Delete appointments
        restTemplate.delete("/api/hospital/appointments/patient/{ssn}", SSN);

        // Step 5: Verify deletion
        ResponseEntity<String> latestResponse = restTemplate.getForEntity(
                "/api/hospital/appointments/latest/{ssn}", String.class, SSN);
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode());

        try {
            ApiResponse<?> apiResponse = objectMapper.readValue(
                    latestResponse.getBody(),
                    new TypeReference<>() {
                    });

            assertTrue(apiResponse.success());
            assertNull(apiResponse.data()); // No appointments should exist
        } catch (Exception e) {
            fail("Failed to parse response: " + e.getMessage());
        }
    }
}