package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.persistence.entity.PrescriptionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain Prescription Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class PrescriptionPersistenceMapper {

    public PrescriptionJpaEntity toJpaEntity(Prescription prescription) {
        if (prescription == null) {
            return null;
        }
        return new PrescriptionJpaEntity(
                prescription.getId().value(),
                prescription.getEncounterId() != null ? prescription.getEncounterId().value() : null,
                prescription.getPatientId(),
                prescription.getPractitionerId(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getRoute(),
                prescription.getDurationDays(),
                prescription.getInstructions(),
                prescription.getStatus().name(),
                prescription.getPrescribedAt(),
                prescription.getExpiresAt(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt()
        );
    }

    public Prescription toDomain(PrescriptionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Prescription.reconstitute(
                PrescriptionId.of(entity.getId()),
                entity.getEncounterId() != null ? EncounterId.of(entity.getEncounterId()) : null,
                entity.getPatientId(),
                entity.getPractitionerId(),
                entity.getMedicationName(),
                entity.getDosage(),
                entity.getFrequency(),
                entity.getRoute(),
                entity.getDurationDays(),
                entity.getInstructions(),
                PrescriptionStatus.valueOf(entity.getStatus()),
                entity.getPrescribedAt(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

