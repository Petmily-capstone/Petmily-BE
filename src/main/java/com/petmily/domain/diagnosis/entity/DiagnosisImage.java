package com.petmily.domain.diagnosis.entity;

import com.petmily.global.entity.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "diagnosis_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisImage extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_request_id", nullable = false)
    private DiagnosisRequest diagnosisRequest;

    @Column(name = "s3_key", nullable = false, columnDefinition = "TEXT")
    private String s3Key;

    @Builder
    private DiagnosisImage(DiagnosisRequest diagnosisRequest, String s3Key) {
        this.diagnosisRequest = diagnosisRequest;
        this.s3Key = s3Key;
    }

    public static DiagnosisImage create(DiagnosisRequest diagnosisRequest, String s3Key) {
        return DiagnosisImage.builder()
                .diagnosisRequest(diagnosisRequest)
                .s3Key(s3Key)
                .build();
    }
}
