package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.repository.EncounterRepository;
import com.his.medcenter.clinical.infrastructure.persistence.entity.EncounterJpaEntity;
import com.his.medcenter.clinical.infrastructure.persistence.mapper.EncounterPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the domain's EncounterRepository port using Spring Data JPA.
 */
@Repository
public class EncounterRepositoryImpl implements EncounterRepository {

    private final SpringDataJpaEncounterRepository jpaRepository;
    private final EncounterPersistenceMapper mapper;

    public EncounterRepositoryImpl(SpringDataJpaEncounterRepository jpaRepository, EncounterPersistenceMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "SpringDataJpaEncounterRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "EncounterPersistenceMapper cannot be null");
    }

    @Override
    public Encounter save(Encounter encounter) {
        EncounterJpaEntity entity = mapper.toJpaEntity(encounter);
        EncounterJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Encounter> findById(EncounterId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Encounter> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Encounter> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Encounter> findByPractitionerId(UUID practitionerId) {
        return jpaRepository.findByPractitionerId(practitionerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Encounter> findByStatus(EncounterStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(EncounterId id) {
        jpaRepository.deleteById(id.value());
    }
}

