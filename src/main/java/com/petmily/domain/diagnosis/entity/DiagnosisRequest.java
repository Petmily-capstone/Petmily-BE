package com.petmily.domain.diagnosis.entity;

import com.petmily.domain.diagnosis.enums.DiagnosisStatus;
import com.petmily.domain.pet.entity.Pet;
import com.petmily.global.entity.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diagnosis_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisRequest extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "symptom_text", columnDefinition = "TEXT")
    private String symptomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DiagnosisStatus status;

    @Builder
    private DiagnosisRequest(Pet pet, String symptomText, DiagnosisStatus status) {
        this.pet = pet;
        this.symptomText = symptomText;
        this.status = status;
    }

    public static DiagnosisRequest create(Pet pet, String symptomText) {
        return DiagnosisRequest.builder()
                .pet(pet)
                .symptomText(symptomText)
                .status(DiagnosisStatus.PENDING)
                .build();
    }
}
