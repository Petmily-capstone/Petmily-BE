package com.petmily.domain.product.dto.response;

import com.petmily.domain.product.entity.Product;
import com.petmily.domain.product.enums.ProductCategory;
import com.petmily.domain.product.enums.TargetSpecies;

public record ProductSummaryResponse(
        Long productId,
        String name,
        String brand,
        ProductCategory category,
        TargetSpecies targetSpecies,
        int price,
        String imageUrl
) {
    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getTargetSpecies(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
}
