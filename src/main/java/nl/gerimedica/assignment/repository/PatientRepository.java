package nl.gerimedica.assignment.repository;

import nl.gerimedica.assignment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Patient entity with added query methods.
 * - Added methods to find and check patients by SSN to avoid inefficient full table scans
 * - Leveraging Spring Data JPA's derived query methods for optimized database access
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    /**
     * Find a patient by their SSN (Social Security Number).
     *
     * @param ssn The SSN to search for
     * @return An Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> findBySsn(String ssn);

    /**
     * Check if a patient with the given SSN exists.
     * More efficient than findBySsn when only existence check is needed.
     *
     * @param ssn The SSN to check
     * @return true if a patient with the given SSN exists, false otherwise
     */
    boolean existsBySsn(String ssn);
}