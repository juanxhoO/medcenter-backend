package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.domain.model.VitalSigns;
import com.his.medcenter.clinical.domain.model.VitalSignsId;
import com.his.medcenter.clinical.domain.repository.VitalSignsRepository;
import com.his.medcenter.clinical.infrastructure.persistence.entity.VitalSignsJpaEntity;
import com.his.medcenter.clinical.infrastructure.persistence.mapper.VitalSignsPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the domain's VitalSignsRepository port using Spring Data JPA.
 */
@Repository
public class VitalSignsRepositoryImpl implements VitalSignsRepository {

    private final SpringDataJpaVitalSignsRepository jpaRepository;
    private final VitalSignsPersistenceMapper mapper;

    public VitalSignsRepositoryImpl(
            SpringDataJpaVitalSignsRepository jpaRepository,
            VitalSignsPersistenceMapper mapper
    ) {
        this.jpaRepository = Objects.requireNonNull(
                jpaRepository,
                "SpringDataJpaVitalSignsRepository cannot be null"
        );
        this.mapper = Objects.requireNonNull(
                mapper,
                "VitalSignsPersistenceMapper cannot be null"
        );
    }

    @Override
    public VitalSigns save(VitalSigns vitalSigns) {
        VitalSignsJpaEntity entity = mapper.toJpaEntity(vitalSigns);
        VitalSignsJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<VitalSigns> findById(VitalSignsId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<VitalSigns> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<VitalSigns> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<VitalSigns> findByPractitionerId(UUID practitionerId) {
        return jpaRepository.findByPractitionerId(practitionerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<VitalSigns> findByEncounterId(UUID encounterId) {
        return jpaRepository.findByEncounterId(encounterId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(VitalSignsId id) {
        jpaRepository.deleteById(id.value());
    }
}