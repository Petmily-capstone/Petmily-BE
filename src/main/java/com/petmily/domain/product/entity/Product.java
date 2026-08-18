package com.petmily.domain.product.entity;

import com.petmily.domain.product.enums.Partner;
import com.petmily.domain.product.enums.ProductCategory;
import com.petmily.domain.product.enums.TargetSpecies;
import com.petmily.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "brand", length = 50)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_species", nullable = false, length = 20)
    private TargetSpecies targetSpecies;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // === 제휴 연동 대비 필드 (현재는 nullable, 미사용) ===

    @Column(name = "purchase_url", columnDefinition = "TEXT")
    private String purchaseUrl;               // 제휴/외부 구매 링크

    @Enumerated(EnumType.STRING)
    @Column(name = "partner", length = 20)
    private Partner partner;                   // 판매처 (COUPANG/NAVER/ETC)

    @Column(name = "external_product_id", length = 100)
    private String externalProductId;          // 외부몰 상품 ID (중복 임포트 방지)

    @Builder
    private Product(String name, String brand, ProductCategory category, TargetSpecies targetSpecies,
                    int price, String imageUrl, String description,
                    String purchaseUrl, Partner partner, String externalProductId) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.targetSpecies = targetSpecies;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.purchaseUrl = purchaseUrl;
        this.partner = partner;
        this.externalProductId = externalProductId;
    }

    public static Product create(String name, String brand, ProductCategory category, TargetSpecies targetSpecies,
                                 int price, String imageUrl, String description) {
        return Product.builder()
                .name(name)
                .brand(brand)
                .category(category)
                .targetSpecies(targetSpecies)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .build();
    }
}
