package com.his.medcenter.patient.domain.model;

import com.his.medcenter.patient.domain.exception.InvalidPatientDataException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a Patient's clinical and demographic profile.
 *
 * <p>Personal identity data (name, email, phone, password, role) lives in the
 * {@code user} module. This aggregate holds only healthcare-specific attributes
 * that a clinician or admissions clerk needs: date of birth, national ID,
 * blood type, insurance, emergency contact, etc.</p>
 *
 * <p>The link to the security/identity layer is a plain {@code userId} (UUID).
 * No imports from the {@code user} module are required.</p>
 */
public class Patient {

    private final PatientId id;
    private final UUID userId;
    private final String medicalRecordNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String nationalId;
    private String address;
    private BloodType bloodType;
    private MaritalStatus maritalStatus;
    private String occupation;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private String allergies;
    private String chronicConditions;
    private final Instant createdAt;
    private Instant updatedAt;

    private Patient(
            PatientId id,
            UUID userId,
            String medicalRecordNumber,
            LocalDate dateOfBirth,
            Gender gender,
            String nationalId,
            String address,
            BloodType bloodType,
            MaritalStatus maritalStatus,
            String occupation,
            String emergencyContactName,
            String emergencyContactPhone,
            String emergencyContactRelationship,
            String insuranceProvider,
            String insurancePolicyNumber,
            String allergies,
            String chronicConditions,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Patient ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.medicalRecordNumber = validateNotBlank(medicalRecordNumber, "Medical record number cannot be blank");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");

        validateDateOfBirth(dateOfBirth);

        this.nationalId = nationalId != null ? nationalId.trim() : null;
        this.address = address != null ? address.trim() : null;
        this.bloodType = bloodType;
        this.maritalStatus = maritalStatus;
        this.occupation = occupation != null ? occupation.trim() : null;
        this.emergencyContactName = emergencyContactName != null ? emergencyContactName.trim() : null;
        this.emergencyContactPhone = emergencyContactPhone != null ? emergencyContactPhone.trim() : null;
        this.emergencyContactRelationship = emergencyContactRelationship != null ? emergencyContactRelationship.trim() : null;
        this.insuranceProvider = insuranceProvider != null ? insuranceProvider.trim() : null;
        this.insurancePolicyNumber = insurancePolicyNumber != null ? insurancePolicyNumber.trim() : null;
        this.allergies = allergies != null ? allergies.trim() : null;
        this.chronicConditions = chronicConditions != null ? chronicConditions.trim() : null;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp cannot be null");
    }

    // ── Factory Methods ──────────────────────────────────────────────────

    /**
     * Creates a new Patient profile linked to an existing User account.
     */
    public static Patient create(
            PatientId id,
            UUID userId,
            String medicalRecordNumber,
            LocalDate dateOfBirth,
            Gender gender,
            String nationalId,
            String address,
            BloodType bloodType,
            MaritalStatus maritalStatus,
            String occupation,
            String emergencyContactName,
            String emergencyContactPhone,
            String emergencyContactRelationship,
            String insuranceProvider,
            String insurancePolicyNumber,
            String allergies,
            String chronicConditions
    ) {
        Instant now = Instant.now();
        return new Patient(
                id, userId, medicalRecordNumber,
                dateOfBirth, gender, nationalId, address,
                bloodType, maritalStatus, occupation,
                emergencyContactName, emergencyContactPhone, emergencyContactRelationship,
                insuranceProvider, insurancePolicyNumber,
                allergies, chronicConditions,
                now, now
        );
    }

    /**
     * Reconstitutes an existing Patient aggregate from persistence.
     */
    public static Patient reconstitute(
            PatientId id,
            UUID userId,
            String medicalRecordNumber,
            LocalDate dateOfBirth,
            Gender gender,
            String nationalId,
            String address,
            BloodType bloodType,
            MaritalStatus maritalStatus,
            String occupation,
            String emergencyContactName,
            String emergencyContactPhone,
            String emergencyContactRelationship,
            String insuranceProvider,
            String insurancePolicyNumber,
            String allergies,
            String chronicConditions,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Patient(
                id, userId, medicalRecordNumber,
                dateOfBirth, gender, nationalId, address,
                bloodType, maritalStatus, occupation,
                emergencyContactName, emergencyContactPhone, emergencyContactRelationship,
                insuranceProvider, insurancePolicyNumber,
                allergies, chronicConditions,
                createdAt, updatedAt
        );
    }

    // ── Business Methods ─────────────────────────────────────────────────

    /**
     * Updates the patient's demographic information.
     */
    public void updateDemographics(
            LocalDate dateOfBirth,
            Gender gender,
            String nationalId,
            String address,
            MaritalStatus maritalStatus,
            String occupation
    ) {
        Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        Objects.requireNonNull(gender, "Gender cannot be null");
        validateDateOfBirth(dateOfBirth);

        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.nationalId = nationalId != null ? nationalId.trim() : null;
        this.address = address != null ? address.trim() : null;
        this.maritalStatus = maritalStatus;
        this.occupation = occupation != null ? occupation.trim() : null;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the patient's clinical information.
     */
    public void updateClinicalInfo(
            BloodType bloodType,
            String allergies,
            String chronicConditions
    ) {
        this.bloodType = bloodType;
        this.allergies = allergies != null ? allergies.trim() : null;
        this.chronicConditions = chronicConditions != null ? chronicConditions.trim() : null;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the emergency contact information.
     */
    public void updateEmergencyContact(
            String contactName,
            String contactPhone,
            String contactRelationship
    ) {
        this.emergencyContactName = contactName != null ? contactName.trim() : null;
        this.emergencyContactPhone = contactPhone != null ? contactPhone.trim() : null;
        this.emergencyContactRelationship = contactRelationship != null ? contactRelationship.trim() : null;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the insurance information.
     */
    public void updateInsurance(
            String insuranceProvider,
            String insurancePolicyNumber
    ) {
        this.insuranceProvider = insuranceProvider != null ? insuranceProvider.trim() : null;
        this.insurancePolicyNumber = insurancePolicyNumber != null ? insurancePolicyNumber.trim() : null;
        this.updatedAt = Instant.now();
    }

    /**
     * Calculates the patient's current age in years.
     */
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // ── Validation ───────────────────────────────────────────────────────

    private static void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new InvalidPatientDataException("Date of birth cannot be in the future");
        }
    }

    private static String validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPatientDataException(message);
        }
        return value.trim();
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public PatientId getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getAddress() {
        return address;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

