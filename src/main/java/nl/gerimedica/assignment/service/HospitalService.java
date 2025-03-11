package nl.gerimedica.assignment.service;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.dto.AppointmentDTO;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import nl.gerimedica.assignment.exception.BadRequestException;
import nl.gerimedica.assignment.exception.ResourceNotFoundException;
import nl.gerimedica.assignment.mappers.AppointmentMapper;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing hospital operations related to patients and appointments
 * <p>
 * This class implements core business logic for the hospital management system including:
 * - Creating and managing patient records
 * - Scheduling appointments
 * - Retrieving appointment information using various criteria
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HospitalService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final MetricsService metricsService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;


    /**
     * Creates multiple appointments for a patient in a single transaction
     *
     * @param patientName Name of the patient
     * @param ssn         Social Security Number of the patient (unique identifier)
     * @param reasons     List of reasons for each appointment
     * @param dates       List of dates for each appointment
     * @return List of created appointment DTOs
     * @throws BadRequestException if input data is invalid
     */
    @Transactional
    @Counted(value = "hospital.service.bulk_create_appointments.count", description = "Count of bulk appointment creation operations")
    public List<AppointmentDTO> bulkCreateAppointments(
            String patientName,
            String ssn,
            List<String> reasons,
            List<String> dates
    ) {
        if (reasons.isEmpty() || dates.isEmpty()) {
            throw new BadRequestException("Reasons and dates lists cannot be empty");
        }
        Patient patient = patientRepository.findBySsn(ssn)
                .map(existingPatient -> {
                    log.info("Using existing patient with SSN: {}", ssn);
                    return existingPatient;
                })
                .orElseGet(() -> {
                    log.info("Creating new patient with SSN: {}", ssn);
                    var newPatient = new Patient(patientName, ssn);
                    patientRepository.save(newPatient);
                    return newPatient;
                });

        List<Appointment> createdAppointments = new ArrayList<>();
        int loopSize = Math.min(reasons.size(), dates.size());

        for (int i = 0; i < loopSize; i++) {
            String reason = reasons.get(i);
            LocalDateTime appointmentDate;

            try {
                appointmentDate = LocalDateTime.parse(dates.get(i), dateFormatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid date format at index " + i + ": " + dates.get(i));
            }

            Appointment appointment = new Appointment(reason, appointmentDate, patient);
            patient.addAppointment(appointment);
            createdAppointments.add(appointment);
        }

        patientRepository.save(patient);
        metricsService.recordAppointmentsCreated(createdAppointments.size());

        createdAppointments.forEach(appt ->
                log.info("""
                                Created appointment:
                                Reason: {}
                                Date: {}
                                Patient: {} (SSN: {})
                                """,
                        appt.getReason(),
                        appt.getAppointmentDate(),
                        appt.getPatient().getName(),
                        appt.getPatient().getSsn())
        );

        return createdAppointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Find a patient by their SSN
     *
     * @param ssn Social Security Number of the patient
     * @return Patient entity
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional(readOnly = true)
    public Patient findPatientBySSN(String ssn) {
        return patientRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with SSN: " + ssn));
    }

    /**
     * Get appointments by exact reason match
     *
     * @param reasonKeyword Reason to search for
     * @return List of appointment DTOs
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByReason(String reasonKeyword) {
        List<Appointment> appointments = appointmentRepository.findByReasonIgnoreCase(reasonKeyword);
        metricsService.recordAppointmentQueried("by_exact_reason");

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments containing the reason keyword
     *
     * @param reasonKeyword Keyword to search for in reason field
     * @return List of appointment DTOs
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsContainingReason(String reasonKeyword) {
        List<Appointment> appointments = appointmentRepository.findByReasonContainingIgnoreCase(reasonKeyword);
        metricsService.recordAppointmentQueried("containing_reason");

        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete all appointments for a patient with the given SSN
     *
     * @param ssn Social Security Number of the patient
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional
    public void deleteAppointmentsBySSN(String ssn) {
        var patientExists = patientRepository.findBySsn(ssn).isPresent();

        if (!patientExists) {
            throw new ResourceNotFoundException("Patient not found with SSN: " + ssn);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientSsn(ssn);
        int count = appointments.size();

        if (count > 0) {
            appointmentRepository.deleteByPatientSsn(ssn);
            metricsService.recordAppointmentsDeleted(count);

            log.info("Deleted {} appointments for patient with SSN: {}", count, ssn);
        }
    }

    /**
     * Find the latest appointment for a patient with the given SSN
     *
     * @param ssn Social Security Number of the patient
     * @return Latest appointment DTO or null if no appointments
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional(readOnly = true)
    public AppointmentDTO findLatestAppointmentBySSN(String ssn) {
        if (!patientRepository.existsBySsn(ssn)) {
            throw new ResourceNotFoundException("Patient not found with SSN: " + ssn);
        }

        metricsService.recordAppointmentQueried("latest_by_ssn");

        return appointmentRepository.findLatestByPatientSsn(ssn, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(appointmentMapper::toDto)
                .orElse(null);
    }
}