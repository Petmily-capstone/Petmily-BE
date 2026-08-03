package com.petmily.domain.diagnosis.entity;

import com.petmily.domain.diagnosis.enums.UrgencyLevel;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diagnosis_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisResult extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_request_id", nullable = false, unique = true)
    private DiagnosisRequest diagnosisRequest;

    @Column(name = "suspected_diseases", columnDefinition = "TEXT")
    private String suspectedDiseases;   // JSON

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false, length = 20)
    private UrgencyLevel urgencyLevel;

    @Column(name = "care_guide", columnDefinition = "TEXT")
    private String careGuide;

    @Column(name = "model_version", nullable = false, length = 20)
    private String modelVersion;

    @Builder
    private DiagnosisResult(DiagnosisRequest diagnosisRequest, String suspectedDiseases,
                            UrgencyLevel urgencyLevel, String careGuide, String modelVersion) {
        this.diagnosisRequest = diagnosisRequest;
        this.suspectedDiseases = suspectedDiseases;
        this.urgencyLevel = urgencyLevel;
        this.careGuide = careGuide;
        this.modelVersion = modelVersion;
    }

    public static DiagnosisResult create(DiagnosisRequest diagnosisRequest, String suspectedDiseases,
                                         UrgencyLevel urgencyLevel, String careGuide, String modelVersion) {
        return DiagnosisResult.builder()
                .diagnosisRequest(diagnosisRequest)
                .suspectedDiseases(suspectedDiseases)
                .urgencyLevel(urgencyLevel)
                .careGuide(careGuide)
                .modelVersion(modelVersion)
                .build();
    }
}
