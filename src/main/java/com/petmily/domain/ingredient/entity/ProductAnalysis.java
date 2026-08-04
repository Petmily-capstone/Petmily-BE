package com.petmily.domain.ingredient.entity;

import com.petmily.domain.pet.entity.Pet;
import com.petmily.domain.product.entity.Product;
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

/**
 * 상품 성분 분석 결과 (특정 반려동물 기준 펫밀리 점수)
 */
@Entity
@Getter
@Table(name = "product_analysis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAnalysis extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_analysis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "good_ingredients", columnDefinition = "TEXT")
    private String goodIngredients;         // JSON

    @Column(name = "caution_ingredients", columnDefinition = "TEXT")
    private String cautionIngredients;      // JSON

    @Column(name = "functional_ingredients", columnDefinition = "TEXT")
    private String functionalIngredients;   // JSON

    @Column(name = "total_score")
    private Integer totalScore;             // 펫밀리 점수 0~100

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Builder
    private ProductAnalysis(Product product, Pet pet, String goodIngredients, String cautionIngredients,
                            String functionalIngredients, Integer totalScore, String summary) {
        this.product = product;
        this.pet = pet;
        this.goodIngredients = goodIngredients;
        this.cautionIngredients = cautionIngredients;
        this.functionalIngredients = functionalIngredients;
        this.totalScore = totalScore;
        this.summary = summary;
    }

    public static ProductAnalysis create(Product product, Pet pet, String goodIngredients, String cautionIngredients,
                                         String functionalIngredients, Integer totalScore, String summary) {
        return ProductAnalysis.builder()
                .product(product)
                .pet(pet)
                .goodIngredients(goodIngredients)
                .cautionIngredients(cautionIngredients)
                .functionalIngredients(functionalIngredients)
                .totalScore(totalScore)
                .summary(summary)
                .build();
    }
}
