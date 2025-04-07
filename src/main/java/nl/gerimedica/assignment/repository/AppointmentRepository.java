package nl.gerimedica.assignment.repository;

import nl.gerimedica.assignment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find appointments by exact reason
     */
    @EntityGraph(attributePaths = {"patient"})
    List<Appointment> findByReasonIgnoreCase(String reason);

    /**
     * Find appointments containing the reason keyword
     */
    @EntityGraph(attributePaths = {"patient"})
    List<Appointment> findByReasonContainingIgnoreCase(String reasonKeyword);

    /**
     * Find appointments by patient SSN
     */
    List<Appointment> findByPatientSsn(String ssn);

    /**
     * Delete all appointments for a patient with the given SSN
     */
    void deleteByPatientSsn(String ssn);

    /**
     * Find the latest appointment for a patient with the given SSN
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.ssn = :ssn ORDER BY a.appointmentDate LIMIT 1")
    Page<Appointment> findLatestByPatientSsn(@Param("ssn") String ssn, Pageable pageable);
}