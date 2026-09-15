package com.petmily.domain.product.repository;

import com.petmily.domain.product.dto.request.ProductSearchCondition;
import com.petmily.domain.product.entity.Product;
import com.petmily.domain.product.enums.TargetSpecies;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 동적 필터 (QueryDSL 미사용 → JPA Specification).
 * 모든 조건은 optional 이며, 값이 있는 조건만 AND 로 결합한다.
 */
public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> search(ProductSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (condition.category() != null) {
                predicates.add(cb.equal(root.get("category"), condition.category()));
            }

            // 대상종 필터: DOG/CAT 선택 시 해당 종 + ALL(전종용/공용)을 함께 노출.
            // ALL 선택 시에는 전종용(ALL) 상품만 노출된다.
            if (condition.targetSpecies() != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("targetSpecies"), condition.targetSpecies()),
                        cb.equal(root.get("targetSpecies"), TargetSpecies.ALL)
                ));
            }

            // 키워드: 상품명 또는 브랜드 부분 일치
            if (StringUtils.hasText(condition.keyword())) {
                String like = "%" + condition.keyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), like),
                        cb.like(root.get("brand"), like)
                ));
            }

            if (condition.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), condition.minPrice()));
            }
            if (condition.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), condition.maxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
