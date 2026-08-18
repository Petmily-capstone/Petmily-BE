package com.petmily.domain.product.dto.response;

import com.petmily.domain.product.entity.Product;
import com.petmily.domain.product.enums.Partner;
import com.petmily.domain.product.enums.ProductCategory;
import com.petmily.domain.product.enums.TargetSpecies;

import java.time.LocalDateTime;

public record ProductDetailResponse(
        Long productId,
        String name,
        String brand,
        ProductCategory category,
        TargetSpecies targetSpecies,
        int price,
        String imageUrl,
        String description,
        String purchaseUrl,
        Partner partner,
        LocalDateTime createdAt
) {
    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getTargetSpecies(),
                product.getPrice(),
                product.getImageUrl(),
                product.getDescription(),
                product.getPurchaseUrl(),
                product.getPartner(),
                product.getCreatedAt()
        );
    }
}
