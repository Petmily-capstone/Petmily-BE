package com.petmily.domain.product.service;

import com.petmily.domain.product.dto.request.ProductSearchCondition;
import com.petmily.domain.product.dto.response.ProductDetailResponse;
import com.petmily.domain.product.dto.response.ProductSummaryResponse;
import com.petmily.domain.product.entity.Product;
import com.petmily.domain.product.exception.ProductErrorCode;
import com.petmily.domain.product.exception.ProductException;
import com.petmily.domain.product.repository.ProductRepository;
import com.petmily.domain.product.repository.ProductSpecification;
import com.petmily.global.apiPayload.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public PageResponse<ProductSummaryResponse> getProducts(ProductSearchCondition condition, Pageable pageable) {
        if (condition.isInvalidPriceRange()) {
            throw new ProductException(ProductErrorCode.INVALID_PRICE_RANGE);
        }
        Page<ProductSummaryResponse> page = productRepository
                .findAll(ProductSpecification.search(condition), pageable)
                .map(ProductSummaryResponse::from);
        return PageResponse.from(page);
    }

    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }
}
