package com.his.medcenter.clinical.infrastructure.persistence.mapper;

import com.his.medcenter.clinical.domain.model.VitalSigns;
import com.his.medcenter.clinical.domain.model.VitalSignsId;
import com.his.medcenter.clinical.infrastructure.persistence.entity.VitalSignsJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain VitalSigns Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class VitalSignsPersistenceMapper {

    public VitalSignsJpaEntity toJpaEntity(VitalSigns vitalSigns) {
        if (vitalSigns == null) {
            return null;
        }

        return new VitalSignsJpaEntity(
                vitalSigns.getId().value(),
                vitalSigns.getPatientId(),
                vitalSigns.getPractitionerId(),
                vitalSigns.getEncounterId(),
                vitalSigns.getTemperature(),
                vitalSigns.getSystolicBloodPressure(),
                vitalSigns.getDiastolicBloodPressure(),
                vitalSigns.getHeartRate(),
                vitalSigns.getRespiratoryRate(),
                vitalSigns.getOxygenSaturation(),
                vitalSigns.getHeight(),
                vitalSigns.getWeight(),
                vitalSigns.getCreatedAt()
        );
    }

    public VitalSigns toDomain(VitalSignsJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return VitalSigns.reconstitute(
                VitalSignsId.of(entity.getId()),
                entity.getPatientId(),
                entity.getPractitionerId(),
                entity.getEncounterId(),
                entity.getTemperature(),
                entity.getSystolicBloodPressure(),
                entity.getDiastolicBloodPressure(),
                entity.getHeartRate(),
                entity.getRespiratoryRate(),
                entity.getOxygenSaturation(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getCreatedAt()
        );
    }
}