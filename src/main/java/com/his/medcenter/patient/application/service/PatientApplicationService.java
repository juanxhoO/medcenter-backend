package com.his.medcenter.patient.application.service;

import com.his.medcenter.patient.application.dto.*;
import com.his.medcenter.patient.application.port.in.CreatePatientUseCase;
import com.his.medcenter.patient.application.port.in.GetPatientUseCase;
import com.his.medcenter.patient.application.port.in.UpdatePatientUseCase;
import com.his.medcenter.patient.domain.exception.PatientAlreadyExistsException;
import com.his.medcenter.patient.domain.exception.PatientNotFoundException;
import com.his.medcenter.patient.domain.model.Patient;
import com.his.medcenter.patient.domain.model.PatientId;
import com.his.medcenter.patient.domain.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating patient-related use cases.
 */
@Service
@Transactional
public class PatientApplicationService implements
        CreatePatientUseCase,
        GetPatientUseCase,
        UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    public PatientApplicationService(PatientRepository patientRepository) {
        this.patientRepository = Objects.requireNonNull(
                patientRepository,
                "PatientRepository cannot be null"
        );
    }

    @Override
    public PatientResponseDto createPatient(CreatePatientCommand command) {
        if (patientRepository.existsByUserId(command.userId())) {
            throw new PatientAlreadyExistsException(
                    "A patient profile already exists for user: " + command.userId()
            );
        }

        if (patientRepository.existsByMedicalRecordNumber(command.medicalRecordNumber())) {
            throw new PatientAlreadyExistsException(
                    "Medical record number already in use: " + command.medicalRecordNumber()
            );
        }

        PatientId newId = PatientId.newUnique();

        Patient patient = Patient.create(
                newId,
                command.userId(),
                command.medicalRecordNumber(),
                command.dateOfBirth(),
                command.gender(),
                command.nationalId(),
                command.address(),
                command.bloodType(),
                command.maritalStatus(),
                command.occupation(),
                command.emergencyContactName(),
                command.emergencyContactPhone(),
                command.emergencyContactRelationship(),
                command.insuranceProvider(),
                command.insurancePolicyNumber(),
                command.allergies(),
                command.chronicConditions()
        );

        Patient saved = patientRepository.save(patient);
        return PatientResponseDto.fromDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(PatientId id) {
        return patientRepository.findById(id)
                .map(PatientResponseDto::fromDomain)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByUserId(UUID userId) {
        return patientRepository.findByUserId(userId)
                .map(PatientResponseDto::fromDomain)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found for user: " + userId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientResponseDto::fromDomain)
                .toList();
    }

    @Override
    public PatientResponseDto updateDemographics(PatientId id, UpdatePatientDemographicsCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.updateDemographics(
                command.dateOfBirth(),
                command.gender(),
                command.nationalId(),
                command.address(),
                command.maritalStatus(),
                command.occupation()
        );

        Patient updated = patientRepository.save(patient);
        return PatientResponseDto.fromDomain(updated);
    }

    @Override
    public PatientResponseDto updateClinicalInfo(PatientId id, UpdatePatientClinicalInfoCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.updateClinicalInfo(
                command.bloodType(),
                command.allergies(),
                command.chronicConditions()
        );

        Patient updated = patientRepository.save(patient);
        return PatientResponseDto.fromDomain(updated);
    }

    @Override
    public PatientResponseDto updateEmergencyContact(PatientId id, UpdateEmergencyContactCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.updateEmergencyContact(
                command.contactName(),
                command.contactPhone(),
                command.contactRelationship()
        );

        Patient updated = patientRepository.save(patient);
        return PatientResponseDto.fromDomain(updated);
    }

    @Override
    public PatientResponseDto updateInsurance(PatientId id, UpdateInsuranceCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.updateInsurance(
                command.insuranceProvider(),
                command.insurancePolicyNumber()
        );

        Patient updated = patientRepository.save(patient);
        return PatientResponseDto.fromDomain(updated);
    }

    public void deletePatient(PatientId id) {
        if (patientRepository.findById(id).isEmpty()) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }
}

