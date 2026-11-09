package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;

import java.util.List;
import java.util.UUID;

public interface GetPrescriptionUseCase {

    PrescriptionResponseDto getPrescriptionById(PrescriptionId id);

    List<PrescriptionResponseDto> getAllPrescriptions();

    List<PrescriptionResponseDto> getPrescriptionsByPatient(UUID patientId);

    List<PrescriptionResponseDto> getPrescriptionsByEncounter(EncounterId encounterId);

    List<PrescriptionResponseDto> getPrescriptionsByPractitioner(UUID practitionerId);

    List<PrescriptionResponseDto> getPrescriptionsByStatus(PrescriptionStatus status);
}

