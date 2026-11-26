package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.DiagnosesId;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.persistence.entity.PrescriptionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PrescriptionPersistenceMapper {

    private final PrescriptionItemPersistenceMapper itemMapper;

    public PrescriptionPersistenceMapper(
            PrescriptionItemPersistenceMapper itemMapper
    ) {
        this.itemMapper = itemMapper;
    }

    /**
     * Maps the Prescription aggregate to its JPA entity,
     * including all associated prescription items.
     */
    public PrescriptionJpaEntity toJpaEntity(Prescription prescription) {

        if (prescription == null) {
            return null;
        }

        var items = prescription.getItems()
                .stream()
                .map(itemMapper::toJpaEntity)
                .toList();

        return new PrescriptionJpaEntity(
                prescription.getId().value(),
                prescription.getDiagnoseId() != null
                        ? prescription.getDiagnoseId().value()
                        : null,
                prescription.getEncounterId() != null
                        ? prescription.getEncounterId().value()
                        : null,
                prescription.getPatientId(),
                prescription.getPractitionerId(),
                prescription.getNotes(),
                prescription.getStatus().name(),
                prescription.getPrescribedAt(),
                prescription.getExpiresAt(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt(),
                items
        );
    }

    /**
     * Reconstitutes the complete Prescription aggregate from persistence,
     * including its associated prescription items.
     */
    public Prescription toDomain(PrescriptionJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        List<com.his.medcenter.clinical.domain.model.PrescriptionItem> items =
                entity.getItems() == null
                        ? Collections.emptyList()
                        : entity.getItems()
                                .stream()
                                .map(itemMapper::toDomain)
                                .toList();

        return Prescription.reconstitute(
                PrescriptionId.of(entity.getId()),

                entity.getDiagnoseId() != null
                        ? DiagnosesId.of(entity.getDiagnoseId())
                        : null,

                entity.getEncounterId() != null
                        ? EncounterId.of(entity.getEncounterId())
                        : null,

                entity.getPatientId(),
                entity.getPractitionerId(),
                entity.getNotes(),
                PrescriptionStatus.valueOf(entity.getStatus()),
                entity.getPrescribedAt(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                items
        );
    }
}