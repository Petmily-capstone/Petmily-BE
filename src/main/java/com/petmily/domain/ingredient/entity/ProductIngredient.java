package com.petmily.domain.ingredient.entity;

import com.petmily.domain.product.entity.Product;
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
 * 상품-성분 연결 (N:M 매핑 + 함량)
 */
@Entity
@Getter
@Table(name = "product_ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ingredient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "amount", length = 50)
    private String amount;   // 함량

    @Builder
    private ProductIngredient(Product product, Ingredient ingredient, String amount) {
        this.product = product;
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public static ProductIngredient create(Product product, Ingredient ingredient, String amount) {
        return ProductIngredient.builder()
                .product(product)
                .ingredient(ingredient)
                .amount(amount)
                .build();
    }
}
