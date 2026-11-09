package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import com.his.medcenter.clinical.infrastructure.persistence.entity.PrescriptionJpaEntity;
import com.his.medcenter.clinical.infrastructure.persistence.mapper.PrescriptionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the domain's PrescriptionRepository port using Spring Data JPA.
 */
@Repository
public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private final SpringDataJpaPrescriptionRepository jpaRepository;
    private final PrescriptionPersistenceMapper mapper;

    public PrescriptionRepositoryImpl(SpringDataJpaPrescriptionRepository jpaRepository, PrescriptionPersistenceMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "SpringDataJpaPrescriptionRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "PrescriptionPersistenceMapper cannot be null");
    }

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionJpaEntity entity = mapper.toJpaEntity(prescription);
        PrescriptionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Prescription> findById(PrescriptionId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Prescription> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Prescription> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Prescription> findByEncounterId(EncounterId encounterId) {
        return jpaRepository.findByEncounterId(encounterId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Prescription> findByPractitionerId(UUID practitionerId) {
        return jpaRepository.findByPractitionerId(practitionerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Prescription> findByStatus(PrescriptionStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(PrescriptionId id) {
        jpaRepository.deleteById(id.value());
    }
}

