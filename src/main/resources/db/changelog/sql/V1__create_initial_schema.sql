CREATE SCHEMA IF NOT EXISTS hospital;

-- Create patients table
CREATE TABLE patients (
                          id BIGSERIAL PRIMARY KEY, -- WHY IT!
                          name VARCHAR(255) NOT NULL,
                          ssn VARCHAR(50) NOT NULL UNIQUE
);

-- Create appointments table
CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,
                              reason VARCHAR(255) NOT NULL,
                              appointment_date TIMESTAMP NOT NULL,
                              patient_id BIGINT NOT NULL,
                              CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
);

-- Create indexes for better query performance
CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_reason ON appointments (reason);
CREATE INDEX idx_appointments_date ON appointments (appointment_date);
