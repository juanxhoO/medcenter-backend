package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.application.service.PrescriptionApplicationService;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.rest.dto.ChangePrescriptionStatusRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreatePrescriptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/clinical/prescriptions", "/api/v1/prescriptions"})
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;
    private final ChangePrescriptionStatusUseCase changePrescriptionStatusUseCase;
    private final PrescriptionApplicationService prescriptionApplicationService;

    public PrescriptionController(
            CreatePrescriptionUseCase createPrescriptionUseCase,
            GetPrescriptionUseCase getPrescriptionUseCase,
            ChangePrescriptionStatusUseCase changePrescriptionStatusUseCase,
            PrescriptionApplicationService prescriptionApplicationService
    ) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
        this.changePrescriptionStatusUseCase = changePrescriptionStatusUseCase;
        this.prescriptionApplicationService = prescriptionApplicationService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponseDto> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request
    ) {
        PrescriptionResponseDto created = createPrescriptionUseCase.createPrescription(request.toCommand());
        URI location = URI.create("/api/v1/clinical/prescriptions/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> getPrescriptionById(@PathVariable UUID id) {
        PrescriptionResponseDto prescription = getPrescriptionUseCase.getPrescriptionById(PrescriptionId.of(id));
        return ResponseEntity.ok(prescription);
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptions(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID encounterId,
            @RequestParam(required = false) UUID practitionerId,
            @RequestParam(required = false) PrescriptionStatus status
    ) {
        if (patientId != null) {
            return ResponseEntity.ok(getPrescriptionUseCase.getPrescriptionsByPatient(patientId));
        }
        if (encounterId != null) {
            return ResponseEntity.ok(getPrescriptionUseCase.getPrescriptionsByEncounter(EncounterId.of(encounterId)));
        }
        if (practitionerId != null) {
            return ResponseEntity.ok(getPrescriptionUseCase.getPrescriptionsByPractitioner(practitionerId));
        }
        if (status != null) {
            return ResponseEntity.ok(getPrescriptionUseCase.getPrescriptionsByStatus(status));
        }
        return ResponseEntity.ok(getPrescriptionUseCase.getAllPrescriptions());
    }

    @PostMapping("/{id}/dispense")
    public ResponseEntity<PrescriptionResponseDto> dispensePrescription(@PathVariable UUID id) {
        PrescriptionResponseDto dispensed = changePrescriptionStatusUseCase.dispensePrescription(PrescriptionId.of(id));
        return ResponseEntity.ok(dispensed);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponseDto> cancelPrescription(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason
    ) {
        PrescriptionResponseDto cancelled = changePrescriptionStatusUseCase.cancelPrescription(PrescriptionId.of(id), reason);
        return ResponseEntity.ok(cancelled);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionResponseDto> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePrescriptionStatusRequest request
    ) {
        PrescriptionId prescriptionId = PrescriptionId.of(id);
        PrescriptionResponseDto updated = switch (request.status()) {
            case DISPENSED -> changePrescriptionStatusUseCase.dispensePrescription(prescriptionId);
            case CANCELLED -> changePrescriptionStatusUseCase.cancelPrescription(prescriptionId, request.reason());
            default -> prescriptionApplicationService.getPrescriptionById(prescriptionId);
        };
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable UUID id) {
        prescriptionApplicationService.deletePrescription(PrescriptionId.of(id));
        return ResponseEntity.noContent().build();
    }
}

