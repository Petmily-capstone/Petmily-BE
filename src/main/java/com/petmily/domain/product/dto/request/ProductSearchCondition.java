package com.petmily.domain.product.dto.request;

import com.petmily.domain.product.enums.ProductCategory;
import com.petmily.domain.product.enums.TargetSpecies;

/**
 * 상품 목록 조회 필터 (모든 필드 optional, @ModelAttribute 로 쿼리 바인딩).
 */
public record ProductSearchCondition(
        ProductCategory category,
        TargetSpecies targetSpecies,
        String keyword,
        Integer minPrice,
        Integer maxPrice
) {
    /** minPrice > maxPrice 이면 잘못된 범위 */
    public boolean isInvalidPriceRange() {
        return minPrice != null && maxPrice != null && minPrice > maxPrice;
    }
}
