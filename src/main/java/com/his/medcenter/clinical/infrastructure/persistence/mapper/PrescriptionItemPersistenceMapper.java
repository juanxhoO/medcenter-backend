package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionItem;
import com.his.medcenter.clinical.domain.model.PrescriptionItemId;
import com.his.medcenter.clinical.domain.model.PrescriptionItemStatus;
import com.his.medcenter.clinical.infrastructure.persistence.entity.PrescriptionItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionItemPersistenceMapper {

    /**
     * Maps a PrescriptionItem domain entity to its JPA entity.
     *
     * The parent PrescriptionJpaEntity is assigned by
     * PrescriptionJpaEntity.addItem().
     */
    public PrescriptionItemJpaEntity toJpaEntity(PrescriptionItem item) {

        if (item == null) {
            return null;
        }

        return new PrescriptionItemJpaEntity(
                item.getId().value(),
                item.getMedicationId(),
                item.getMedicationName(),
                item.getDosage(),
                item.getFrequency(),
                item.getRoute(),
                item.getDurationDays(),
                item.getQuantity(),
                item.getInstructions(),
                item.getStatus().name(),
                item.getCancellationReason(),
                item.getCancelledAt(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    /**
     * Reconstitutes a PrescriptionItem domain entity from persistence.
     *
     * The PrescriptionId is obtained from the parent JPA entity.
     */
    public PrescriptionItem toDomain(PrescriptionItemJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        if (entity.getPrescription() == null) {
            throw new IllegalStateException(
                    "PrescriptionItem must belong to a Prescription"
            );
        }

        return PrescriptionItem.reconstitute(
                PrescriptionItemId.of(entity.getId()),
                PrescriptionId.of(entity.getPrescription().getId()),
                entity.getMedicationId(),
                entity.getMedicationName(),
                entity.getDosage(),
                entity.getFrequency(),
                entity.getRoute(),
                entity.getDurationDays(),
                entity.getQuantity(),
                entity.getInstructions(),
                PrescriptionItemStatus.valueOf(entity.getStatus()),
                entity.getCancellationReason(),
                entity.getCancelledAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}