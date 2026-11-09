package com.his.medcenter.clinical.application.service;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.domain.exception.PrescriptionNotFoundException;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating prescription-related use cases and domain logic.
 */
@Service
@Transactional
public class PrescriptionApplicationService implements
        CreatePrescriptionUseCase,
        GetPrescriptionUseCase,
        ChangePrescriptionStatusUseCase {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionApplicationService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = Objects.requireNonNull(prescriptionRepository, "PrescriptionRepository cannot be null");
    }

    @Override
    public PrescriptionResponseDto createPrescription(CreatePrescriptionCommand command) {
        PrescriptionId newId = PrescriptionId.newUnique();
        EncounterId encounterId = command.encounterId() != null
                ? EncounterId.of(command.encounterId())
                : null;

        Prescription prescription = Prescription.create(
                newId,
                encounterId,
                command.patientId(),
                command.practitionerId(),
                command.medicationName(),
                command.dosage(),
                command.frequency(),
                command.route(),
                command.durationDays(),
                command.instructions()
        );

        Prescription saved = prescriptionRepository.save(prescription);
        return PrescriptionResponseDto.fromDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionById(PrescriptionId id) {
        return prescriptionRepository.findById(id)
                .map(PrescriptionResponseDto::fromDomain)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByPatient(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByEncounter(EncounterId encounterId) {
        return prescriptionRepository.findByEncounterId(encounterId).stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByPractitioner(UUID practitionerId) {
        return prescriptionRepository.findByPractitionerId(practitionerId).stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByStatus(PrescriptionStatus status) {
        return prescriptionRepository.findByStatus(status).stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    @Override
    public PrescriptionResponseDto dispensePrescription(PrescriptionId id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));

        prescription.dispense();
        Prescription updated = prescriptionRepository.save(prescription);
        return PrescriptionResponseDto.fromDomain(updated);
    }

    @Override
    public PrescriptionResponseDto cancelPrescription(PrescriptionId id, String reason) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));

        prescription.cancel(reason);
        Prescription updated = prescriptionRepository.save(prescription);
        return PrescriptionResponseDto.fromDomain(updated);
    }

    public void deletePrescription(PrescriptionId id) {
        if (prescriptionRepository.findById(id).isEmpty()) {
            throw new PrescriptionNotFoundException(id);
        }
        prescriptionRepository.deleteById(id);
    }
}

