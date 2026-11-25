package com.his.medcenter.patient.infrastructure.persistence.mapper;

import com.his.medcenter.patient.domain.model.*;
import com.his.medcenter.patient.infrastructure.persistence.entity.PatientJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain Patient Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class PatientPersistenceMapper {

    public PatientJpaEntity toJpaEntity(Patient patient) {
        if (patient == null) {
            return null;
        }
        return new PatientJpaEntity(
                patient.getId().value(),
                patient.getUserId(),
                patient.getMedicalRecordNumber(),
                patient.getDateOfBirth(),
                patient.getGender().name(),
                patient.getNationalId(),
                patient.getAddress(),
                patient.getBloodType() != null ? patient.getBloodType().name() : null,
                patient.getMaritalStatus() != null ? patient.getMaritalStatus().name() : null,
                patient.getOccupation(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getEmergencyContactRelationship(),
                patient.getInsuranceProvider(),
                patient.getInsurancePolicyNumber(),
                patient.getAllergies(),
                patient.getChronicConditions(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    public Patient toDomain(PatientJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Patient.reconstitute(
                PatientId.of(entity.getId()),
                entity.getUserId(),
                entity.getMedicalRecordNumber(),
                entity.getDateOfBirth(),
                Gender.valueOf(entity.getGender()),
                entity.getNationalId(),
                entity.getAddress(),
                entity.getBloodType() != null ? BloodType.valueOf(entity.getBloodType()) : null,
                entity.getMaritalStatus() != null ? MaritalStatus.valueOf(entity.getMaritalStatus()) : null,
                entity.getOccupation(),
                entity.getEmergencyContactName(),
                entity.getEmergencyContactPhone(),
                entity.getEmergencyContactRelationship(),
                entity.getInsuranceProvider(),
                entity.getInsurancePolicyNumber(),
                entity.getAllergies(),
                entity.getChronicConditions(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

