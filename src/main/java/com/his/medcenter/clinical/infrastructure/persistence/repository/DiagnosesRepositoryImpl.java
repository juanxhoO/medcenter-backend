package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.domain.model.Diagnoses;
import com.his.medcenter.clinical.domain.model.DiagnosesId;
import com.his.medcenter.clinical.domain.model.DiagnosesType;
import com.his.medcenter.clinical.domain.repository.DiagnosesRepository;
import com.his.medcenter.clinical.infrastructure.persistence.entity.DiagnosesJpaEntity;
import com.his.medcenter.clinical.infrastructure.persistence.mapper.DiagnosesPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the domain's DiagnosesRepository port using Spring Data JPA.
 */
@Repository
public class DiagnosesRepositoryImpl implements DiagnosesRepository {

    private final SpringDataJpaDiagnosesRepository jpaRepository;
    private final DiagnosesPersistenceMapper mapper;

    public DiagnosesRepositoryImpl(
            SpringDataJpaDiagnosesRepository jpaRepository,
            DiagnosesPersistenceMapper mapper
    ) {
        this.jpaRepository = Objects.requireNonNull(
                jpaRepository,
                "SpringDataJpaDiagnosesRepository cannot be null"
        );
        this.mapper = Objects.requireNonNull(
                mapper,
                "DiagnosesPersistenceMapper cannot be null"
        );
    }

    @Override
    public Diagnoses save(Diagnoses diagnosis) {
        DiagnosesJpaEntity entity = mapper.toJpaEntity(diagnosis);
        DiagnosesJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Diagnoses> findById(DiagnosesId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Diagnoses> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Diagnoses> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Diagnoses> findByPractitionerId(UUID practitionerId) {
        return jpaRepository.findByPractitionerId(practitionerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Diagnoses> findByEncounterId(UUID encounterId) {
        return jpaRepository.findByEncounterId(encounterId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Diagnoses> findByType(DiagnosesType type) {
        return jpaRepository.findByType(type.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(DiagnosesId id) {
        jpaRepository.deleteById(id.value());
    }
}