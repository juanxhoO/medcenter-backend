package com.his.medcenter.clinical.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity representing the persistent database representation
 * of a Prescription aggregate.
 *
 * A prescription contains multiple medication items.
 */
@Entity
@Table(name = "prescriptions")
public class PrescriptionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "diagnoses_id")
    private UUID diagnoseId;

    @Column(name = "encounter_id")
    private UUID encounterId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "practitioner_id", nullable = false)
    private UUID practitionerId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "prescribed_at", nullable = false, updatable = false)
    private Instant prescribedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Medication items belonging to this prescription.
     *
     * Items are not physically deleted when removed from the collection.
     * Cancellation is represented through their own status.
     */
    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    private List<PrescriptionItemJpaEntity> items = new ArrayList<>();

    public PrescriptionJpaEntity() {
    }

    public PrescriptionJpaEntity(
            UUID id,
            UUID diagnoseId,
            UUID encounterId,
            UUID patientId,
            UUID practitionerId,
            String notes,
            String status,
            Instant prescribedAt,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt,
            List<PrescriptionItemJpaEntity> items
    ) {
        this.id = id;
        this.diagnoseId = diagnoseId;
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.practitionerId = practitionerId;
        this.notes = notes;
        this.status = status;
        this.prescribedAt = prescribedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        if (items != null) {
            items.forEach(this::addItem);
        }
    }

    /**
     * Maintains both sides of the bidirectional relationship.
     */
    public void addItem(PrescriptionItemJpaEntity item) {
        items.add(item);
        item.setPrescription(this);
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDiagnoseId() {
        return diagnoseId;
    }

    public void setDiagnoseId(UUID diagnoseId) {
        this.diagnoseId = diagnoseId;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(UUID encounterId) {
        this.encounterId = encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(UUID practitionerId) {
        this.practitionerId = practitionerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPrescribedAt() {
        return prescribedAt;
    }

    public void setPrescribedAt(Instant prescribedAt) {
        this.prescribedAt = prescribedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PrescriptionItemJpaEntity> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemJpaEntity> items) {
        this.items.clear();

        if (items != null) {
            items.forEach(this::addItem);
        }
    }
}