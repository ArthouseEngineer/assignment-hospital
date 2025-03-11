-- Insert sample patients
INSERT INTO patients (id, name, ssn) VALUES
                                         (1, 'John Smith', '123-45-6789'),
                                         (2, 'Jane Doe', '987-65-4321'),
                                         (3, 'Bob Johnson', '555-55-5555');

-- Reset the sequence to continue after our inserted ids
SELECT setval('patients_id_seq', (SELECT MAX(id) FROM patients));

-- Insert sample appointments
INSERT INTO appointments (id, reason, appointment_date, patient_id) VALUES
                                                                        (1, 'Annual Checkup', '2025-03-10 10:00:00', 1),
                                                                        (2, 'Flu Symptoms', '2023-09-20 14:30:00', 1),
                                                                        (3, 'Annual Checkup', '2023-07-10 09:15:00', 2),
                                                                        (4, 'Blood Test', '2023-08-05 11:45:00', 2),
                                                                        (5, 'Vaccination', '2023-10-01 16:00:00', 3);

-- Reset the sequence to continue after our inserted ids
SELECT setval('appointments_id_seq', (SELECT MAX(id) FROM appointments));