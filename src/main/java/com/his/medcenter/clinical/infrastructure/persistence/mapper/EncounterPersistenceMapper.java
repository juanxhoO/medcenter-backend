package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;
import com.his.medcenter.clinical.infrastructure.persistence.entity.EncounterJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain Encounter Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class EncounterPersistenceMapper {

    public EncounterJpaEntity toJpaEntity(Encounter encounter) {
        if (encounter == null) {
            return null;
        }
        return new EncounterJpaEntity(
                encounter.getId().value(),
                encounter.getPatientId(),
                encounter.getPractitionerId(),
                encounter.getType().name(),
                encounter.getStatus().name(),
                encounter.getPriority().name(),
                encounter.getReason(),
                encounter.getDiagnosis(),
                encounter.getNotes(),
                encounter.getStartedAt(),
                encounter.getEndedAt(),
                encounter.getCreatedAt(),
                encounter.getUpdatedAt()
        );
    }

    public Encounter toDomain(EncounterJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Encounter.reconstitute(
                EncounterId.of(entity.getId()),
                entity.getPatientId(),
                entity.getPractitionerId(),
                EncounterType.valueOf(entity.getType()),
                EncounterStatus.valueOf(entity.getStatus()),
                EncounterPriority.valueOf(entity.getPriority()),
                entity.getReason(),
                entity.getDiagnosis(),
                entity.getNotes(),
                entity.getStartedAt(),
                entity.getEndedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

