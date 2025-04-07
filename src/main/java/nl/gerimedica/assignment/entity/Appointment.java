package nl.gerimedica.assignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a scheduled appointment for a patient
 *
 * Key features:
 * - Stores appointment reason and date
 * - Many-to-one relationship with Patient
 * - Uses LocalDateTime for proper date/time handling
 * - JPA annotations for persistence
 * - Bean validation constraints for data integrity
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Reason is required")
    @Column(nullable = false)
    private String reason;

    @NotNull(message = "Appointment date is required")
    @Column(nullable = false) // WHY can remove it?
    private LocalDateTime appointmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public Appointment(String reason, LocalDateTime appointmentDate, Patient patient) {
        this.reason = reason;
        this.appointmentDate = appointmentDate;
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}