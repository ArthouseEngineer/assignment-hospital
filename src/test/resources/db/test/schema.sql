-- Drop tables if they exist to ensure clean state
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;

-- Create patients table
CREATE TABLE patients (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          ssn VARCHAR(50) NOT NULL,
                          CONSTRAINT uk_patients_ssn UNIQUE (ssn)
);

-- Create appointments table
CREATE TABLE appointments (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              reason VARCHAR(255) NOT NULL,
                              appointment_date TIMESTAMP NOT NULL,
                              patient_id BIGINT NOT NULL,
                              CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
);

-- Create indexes for better query performance
CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_reason ON appointments (reason);
CREATE INDEX idx_appointments_date ON appointments (appointment_date);
