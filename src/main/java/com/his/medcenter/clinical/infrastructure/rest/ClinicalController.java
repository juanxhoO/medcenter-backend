package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.ClinicalSummaryDto;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.GetEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller aggregating clinical patient records and summaries.
 */
@RestController
@RequestMapping("/api/v1/clinical")
public class ClinicalController {

    private final GetEncounterUseCase getEncounterUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;

    public ClinicalController(
            GetEncounterUseCase getEncounterUseCase,
            GetPrescriptionUseCase getPrescriptionUseCase
    ) {
        this.getEncounterUseCase = getEncounterUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
    }

    @GetMapping("/patient/{patientId}/summary")
    public ResponseEntity<ClinicalSummaryDto> getPatientClinicalSummary(@PathVariable UUID patientId) {
        List<EncounterResponseDto> encounters = getEncounterUseCase.getEncountersByPatient(patientId);
        List<PrescriptionResponseDto> prescriptions = getPrescriptionUseCase.getPrescriptionsByPatient(patientId);

        ClinicalSummaryDto summary = new ClinicalSummaryDto(patientId, encounters, prescriptions);
        return ResponseEntity.ok(summary);
    }
}

