package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.Diagnoses;
import com.his.medcenter.clinical.domain.model.DiagnosesId;
import com.his.medcenter.clinical.domain.model.DiagnosesType;
import com.his.medcenter.clinical.infrastructure.persistence.entity.DiagnosesJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain Diagnoses Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class DiagnosesPersistenceMapper {

    public DiagnosesJpaEntity toJpaEntity(Diagnoses diagnosis) {
        if (diagnosis == null) {
            return null;
        }

        return new DiagnosesJpaEntity(
                diagnosis.getId().value(),
                diagnosis.getPatientId(),
                diagnosis.getPractitionerId(),
                diagnosis.getEncounterId(),
                diagnosis.getType().name(),
                diagnosis.getDescription(),
                diagnosis.getCreatedAt()
        );
    }

    public Diagnoses toDomain(DiagnosesJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Diagnoses.reconstitute(
                DiagnosesId.of(entity.getId()),
                entity.getPatientId(),
                entity.getPractitionerId(),
                entity.getEncounterId(),
                DiagnosesType.valueOf(entity.getType()),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}