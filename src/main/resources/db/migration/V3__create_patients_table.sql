CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    medical_record_number VARCHAR(50) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    national_id VARCHAR(50) UNIQUE,
    address VARCHAR(500),
    blood_type VARCHAR(20),
    marital_status VARCHAR(20),
    occupation VARCHAR(100),
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(30),
    emergency_contact_relationship VARCHAR(50),
    insurance_provider VARCHAR(200),
    insurance_policy_number VARCHAR(100),
    allergies TEXT,
    chronic_conditions TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(user_id);
CREATE INDEX IF NOT EXISTS idx_patients_medical_record_number ON patients(medical_record_number);
CREATE INDEX IF NOT EXISTS idx_patients_national_id ON patients(national_id);

