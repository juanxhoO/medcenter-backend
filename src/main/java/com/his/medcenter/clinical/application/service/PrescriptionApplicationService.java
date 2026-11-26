package com.his.medcenter.clinical.application.service;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.CreatePrescriptionItemCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionNotFoundException;
import com.his.medcenter.clinical.domain.model.DiagnosesId;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionItem;
import com.his.medcenter.clinical.domain.model.PrescriptionItemId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating prescription-related use cases.
 *
 * The Prescription aggregate owns its medication items.
 * Pharmacy manages inventory and actual medication dispensation.
 */
@Service
@Transactional
public class PrescriptionApplicationService implements
        ChangePrescriptionStatusUseCase,
        CreatePrescriptionUseCase,
        GetPrescriptionUseCase {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionApplicationService(
            PrescriptionRepository prescriptionRepository
    ) {
        this.prescriptionRepository = Objects.requireNonNull(
                prescriptionRepository,
                "PrescriptionRepository cannot be null"
        );
    }

    // ---------------------------------------------------------
    // CREATE PRESCRIPTION
    // ---------------------------------------------------------

    @Override
    public PrescriptionResponseDto createPrescription(
            CreatePrescriptionCommand command
    ) {
        Objects.requireNonNull(command, "Command cannot be null");

        if (command.items() == null || command.items().isEmpty()) {
            throw new InvalidPrescriptionDataException(
                    "A prescription must contain at least one medication item"
            );
        }

        PrescriptionId prescriptionId = PrescriptionId.newUnique();

        EncounterId encounterId = command.encounterId() != null
                ? EncounterId.of(command.encounterId())
                : null;

        DiagnosesId diagnoseId = command.diagnoseId() != null
                ? DiagnosesId.of(command.diagnoseId())
                : null;

        // Create prescription aggregate.
        Prescription prescription = Prescription.create(
                prescriptionId,
                diagnoseId,
                encounterId,
                command.patientId(),
                command.practitionerId(),
                command.notes()
        );

        // Add medication items to the aggregate.
        for (CreatePrescriptionItemCommand itemCommand : command.items()) {

            PrescriptionItem item = createPrescriptionItem(
                    prescriptionId,
                    itemCommand
            );

            prescription.addItem(item);
        }

        Prescription saved = prescriptionRepository.save(prescription);

        return PrescriptionResponseDto.fromDomain(saved);
    }

    // ---------------------------------------------------------
    // ADD PRESCRIPTION ITEM
    // ---------------------------------------------------------

    public PrescriptionResponseDto addPrescriptionItem(
            PrescriptionId prescriptionId,
            CreatePrescriptionItemCommand command
    ) {
        Objects.requireNonNull(command, "Command cannot be null");

        Prescription prescription = findPrescription(prescriptionId);

        PrescriptionItem item = createPrescriptionItem(
                prescriptionId,
                command
        );

        prescription.addItem(item);

        Prescription updated = prescriptionRepository.save(prescription);

        return PrescriptionResponseDto.fromDomain(updated);
    }

    // ---------------------------------------------------------
    // CANCEL INDIVIDUAL PRESCRIPTION ITEM
    // ---------------------------------------------------------

    public PrescriptionResponseDto cancelPrescriptionItem(
            PrescriptionId prescriptionId,
            PrescriptionItemId itemId,
            String reason
    ) {
        Prescription prescription = findPrescription(prescriptionId);

        prescription.cancelItem(itemId, reason);

        Prescription updated = prescriptionRepository.save(prescription);

        return PrescriptionResponseDto.fromDomain(updated);
    }

    // ---------------------------------------------------------
    // GET PRESCRIPTION
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionById(
            PrescriptionId id
    ) {
        Prescription prescription = findPrescription(id);

        return PrescriptionResponseDto.fromDomain(prescription);
    }

    // ---------------------------------------------------------
    // GET ALL PRESCRIPTIONS
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    // ---------------------------------------------------------
    // GET PRESCRIPTIONS BY PATIENT
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByPatient(
            UUID patientId
    ) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    // ---------------------------------------------------------
    // GET PRESCRIPTIONS BY ENCOUNTER
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByEncounter(
            EncounterId encounterId
    ) {
        return prescriptionRepository.findByEncounterId(encounterId)
                .stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    // ---------------------------------------------------------
    // GET PRESCRIPTIONS BY PRACTITIONER
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByPractitioner(
            UUID practitionerId
    ) {
        return prescriptionRepository.findByPractitionerId(practitionerId)
                .stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    // ---------------------------------------------------------
    // GET PRESCRIPTIONS BY STATUS
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByStatus(
            PrescriptionStatus status
    ) {
        return prescriptionRepository.findByStatus(status)
                .stream()
                .map(PrescriptionResponseDto::fromDomain)
                .toList();
    }

    // ---------------------------------------------------------
    // CANCEL ENTIRE PRESCRIPTION
    // ---------------------------------------------------------

    public PrescriptionResponseDto cancelPrescription(
            PrescriptionId id,
            String reason
    ) {
        Prescription prescription = findPrescription(id);

        prescription.cancel(reason);

        Prescription updated = prescriptionRepository.save(prescription);

        return PrescriptionResponseDto.fromDomain(updated);
    }

    // ---------------------------------------------------------
    // PRIVATE METHODS
    // ---------------------------------------------------------

    /**
     * Creates either a catalog medication item or an external medication item.
     */
    private PrescriptionItem createPrescriptionItem(
            PrescriptionId prescriptionId,
            CreatePrescriptionItemCommand command
    ) {
        PrescriptionItemId itemId = PrescriptionItemId.newUnique();

        if (command.medicationId() != null) {
            return PrescriptionItem.create(
                    itemId,
                    prescriptionId,
                    command.medicationId(),
                    command.medicationName(),
                    command.dosage(),
                    command.frequency(),
                    command.route(),
                    command.durationDays(),
                    command.quantity(),
                    command.instructions()
            );
        }

        return PrescriptionItem.createExternal(
                itemId,
                prescriptionId,
                command.medicationName(),
                command.dosage(),
                command.frequency(),
                command.route(),
                command.durationDays(),
                command.quantity(),
                command.instructions()
        );
    }

    /**
     * Retrieves a prescription or throws an exception.
     */
    private Prescription findPrescription(PrescriptionId id) {
        Objects.requireNonNull(id, "Prescription ID cannot be null");

        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));
    }
}