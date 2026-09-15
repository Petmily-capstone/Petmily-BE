package com.petmily.domain.product.controller;

import com.petmily.domain.product.dto.request.ProductSearchCondition;
import com.petmily.domain.product.dto.response.ProductDetailResponse;
import com.petmily.domain.product.dto.response.ProductSummaryResponse;
import com.petmily.domain.product.service.ProductService;
import com.petmily.global.apiPayload.ApiResponse;
import com.petmily.global.apiPayload.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품", description = "상품 조회 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "카테고리·대상종·키워드·가격 필터와 페이지네이션으로 상품 목록을 조회한다.")
    @GetMapping
    public ApiResponse<PageResponse<ProductSummaryResponse>> getProducts(
            @ParameterObject @ModelAttribute ProductSearchCondition condition,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.onSuccess(productService.getProducts(condition, pageable));
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회한다.")
    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.onSuccess(productService.getProduct(productId));
    }
}
