package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.rest.dto.ChangePrescriptionStatusRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreatePrescriptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;
    private final ChangePrescriptionStatusUseCase changePrescriptionStatusUseCase;

    public PrescriptionController(
            CreatePrescriptionUseCase createPrescriptionUseCase,
            GetPrescriptionUseCase getPrescriptionUseCase,
            ChangePrescriptionStatusUseCase changePrescriptionStatusUseCase
    ) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
        this.changePrescriptionStatusUseCase = changePrescriptionStatusUseCase;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponseDto> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request
    ) {
        PrescriptionResponseDto created =
                createPrescriptionUseCase.createPrescription(request.toCommand());

        URI location = URI.create("/api/v1/prescriptions/" + created.id());

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> getPrescriptionById(
            @PathVariable UUID id
    ) {
        PrescriptionResponseDto prescription =
                getPrescriptionUseCase.getPrescriptionById(PrescriptionId.of(id));

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
            return ResponseEntity.ok(
                    getPrescriptionUseCase.getPrescriptionsByPatient(patientId)
            );
        }

        if (encounterId != null) {
            return ResponseEntity.ok(
                    getPrescriptionUseCase.getPrescriptionsByEncounter(
                            EncounterId.of(encounterId)
                    )
            );
        }

        if (practitionerId != null) {
            return ResponseEntity.ok(
                    getPrescriptionUseCase.getPrescriptionsByPractitioner(practitionerId)
            );
        }

        if (status != null) {
            return ResponseEntity.ok(
                    getPrescriptionUseCase.getPrescriptionsByStatus(status)
            );
        }

        return ResponseEntity.ok(
                getPrescriptionUseCase.getAllPrescriptions()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionResponseDto> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePrescriptionStatusRequest request
    ) {
        PrescriptionId prescriptionId = PrescriptionId.of(id);

        if (request.status() != PrescriptionStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Clinical only supports prescription cancellation through this endpoint"
            );
        }

        PrescriptionResponseDto updated =
                changePrescriptionStatusUseCase.cancelPrescription(
                        prescriptionId,
                        request.reason()
                );

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponseDto> cancelPrescription(
            @PathVariable UUID id,
            @RequestParam String reason
    ) {
        PrescriptionResponseDto cancelled =
                changePrescriptionStatusUseCase.cancelPrescription(
                        PrescriptionId.of(id),
                        reason
                );

        return ResponseEntity.ok(cancelled);
    }
}