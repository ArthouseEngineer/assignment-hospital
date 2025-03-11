-- Create patients table
CREATE TABLE patients (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          ssn VARCHAR(50) NOT NULL,
                          CONSTRAINT uk_patients_ssn UNIQUE (ssn)
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

-- Add comment on tables
COMMENT ON TABLE patients IS 'Stores patient information';
COMMENT ON TABLE appointments IS 'Stores appointment information';

-- Add comments on columns
COMMENT ON COLUMN patients.id IS 'Primary key';
COMMENT ON COLUMN patients.name IS 'Patient name';
COMMENT ON COLUMN patients.ssn IS 'Social Security Number (unique identifier)';

COMMENT ON COLUMN appointments.id IS 'Primary key';
COMMENT ON COLUMN appointments.reason IS 'Reason for the appointment';
COMMENT ON COLUMN appointments.appointment_date IS 'Date and time of the appointment';
COMMENT ON COLUMN appointments.patient_id IS 'Reference to the patient';