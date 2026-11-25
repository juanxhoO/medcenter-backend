package com.his.medcenter.patient.infrastructure.persistence.repository;

import com.his.medcenter.patient.domain.model.Patient;
import com.his.medcenter.patient.domain.model.PatientId;
import com.his.medcenter.patient.domain.repository.PatientRepository;
import com.his.medcenter.patient.infrastructure.persistence.entity.PatientJpaEntity;
import com.his.medcenter.patient.infrastructure.persistence.mapper.PatientPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the domain's PatientRepository port using Spring Data JPA.
 */
@Repository
public class PatientRepositoryImpl implements PatientRepository {

    private final SpringDataJpaPatientRepository jpaRepository;
    private final PatientPersistenceMapper mapper;

    public PatientRepositoryImpl(
            SpringDataJpaPatientRepository jpaRepository,
            PatientPersistenceMapper mapper
    ) {
        this.jpaRepository = Objects.requireNonNull(
                jpaRepository,
                "SpringDataJpaPatientRepository cannot be null"
        );
        this.mapper = Objects.requireNonNull(
                mapper,
                "PatientPersistenceMapper cannot be null"
        );
    }

    @Override
    public Patient save(Patient patient) {
        PatientJpaEntity entity = mapper.toJpaEntity(patient);
        PatientJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(PatientId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByMedicalRecordNumber(String mrn) {
        return jpaRepository.findByMedicalRecordNumber(mrn)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByNationalId(String nationalId) {
        return jpaRepository.findByNationalId(nationalId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Patient> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByMedicalRecordNumber(String mrn) {
        return jpaRepository.existsByMedicalRecordNumber(mrn);
    }

    @Override
    public void deleteById(PatientId id) {
        jpaRepository.deleteById(id.value());
    }
}

