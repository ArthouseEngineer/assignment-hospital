-- Test data for integration tests

INSERT INTO patients (id, name, ssn) VALUES
                                         (1, 'John Doe', '123-45-6789'),
                                         (2, 'Jane Smith', '987-65-4321'),
                                         (3, 'Test Patient', '123-test-456'),
                                         (4, 'No Appointments', 'no-appointments-ssn');

INSERT INTO appointments (id, reason, appointment_date, patient_id) VALUES
                                                                        (1, 'Annual Checkup', DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 1),  -- 7 days from now
                                                                        (2, 'Flu Shot', DATEADD('DAY', 14, CURRENT_TIMESTAMP()), 1),       -- 14 days from now
                                                                        (3, 'Blood Test', DATEADD('DAY', 3, CURRENT_TIMESTAMP()), 2),       -- 3 days from now
                                                                        (4, 'Test Reason', DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 3);     -- 7 days from now