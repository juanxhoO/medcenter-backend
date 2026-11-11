package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.infrastructure.persistence.entity.EncounterJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaEncounterRepository extends JpaRepository<EncounterJpaEntity, UUID> {

    List<EncounterJpaEntity> findByPatientId(UUID patientId);

    List<EncounterJpaEntity> findByPractitionerId(UUID practitionerId);

    List<EncounterJpaEntity> findByStatus(String status);
}

