package com.petmily.domain.product.init;

import com.petmily.domain.product.entity.Product;
import com.petmily.domain.product.enums.ProductCategory;
import com.petmily.domain.product.enums.TargetSpecies;
import com.petmily.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 개발용 상품 큐레이션 시더. (local 프로파일에서 상품 테이블이 비어 있을 때만 1회 주입)
 * 실제 존재하는 사료/영양제/간식을 기준으로 하되, 이미지/구매링크는 제휴 연동 전까지 비워둔다.
 */
@Slf4j
@Order(1)
@Component
@Profile("local")
@RequiredArgsConstructor
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = List.of(
                // === 사료 (FOOD) ===
                Product.create("로얄캐닌 미니 어덜트", "로얄캐닌",
                        ProductCategory.FOOD, TargetSpecies.DOG, 28000, null, "소형견 성견용 건식 사료"),
                Product.create("오리젠 오리지널 독", "오리젠",
                        ProductCategory.FOOD, TargetSpecies.DOG, 42000, null, "고단백 그레인프리 전연령 사료"),
                Product.create("아카나 퍼피 스몰 브리드", "아카나",
                        ProductCategory.FOOD, TargetSpecies.DOG, 39000, null, "소형견 자견용 사료"),
                Product.create("힐스 사이언스다이어트 어덜트", "힐스",
                        ProductCategory.FOOD, TargetSpecies.DOG, 35000, null, "성견 건강 유지용 사료"),
                Product.create("로얄캐닌 인도어", "로얄캐닌",
                        ProductCategory.FOOD, TargetSpecies.CAT, 31000, null, "실내묘 성묘용 건식 사료"),
                Product.create("지위픽 캣 에어드라이", "지위픽",
                        ProductCategory.FOOD, TargetSpecies.CAT, 45000, null, "고단백 에어드라이 사료"),
                Product.create("나우프레시 그레인프리 캣", "나우프레시",
                        ProductCategory.FOOD, TargetSpecies.CAT, 38000, null, "곡물 무첨가 전연령 사료"),

                // === 영양제 (SUPPLEMENT) ===
                Product.create("벳아이닥터 오메가3", "벳아이닥터",
                        ProductCategory.SUPPLEMENT, TargetSpecies.ALL, 22000, null, "피부/모질 개선 오메가3 보충제"),
                Product.create("종근당 유디스 프로바이오틱스", "종근당건강",
                        ProductCategory.SUPPLEMENT, TargetSpecies.ALL, 18000, null, "장 건강 유산균"),
                Product.create("뉴트리코어 글루코사민", "뉴트리코어",
                        ProductCategory.SUPPLEMENT, TargetSpecies.DOG, 25000, null, "관절 건강 글루코사민/콘드로이틴"),

                // === 간식 (SNACK) ===
                Product.create("이나바 챠오츄르 참치", "이나바",
                        ProductCategory.SNACK, TargetSpecies.CAT, 12000, null, "고양이 액상 간식"),
                Product.create("덴티페어리 치석 케어 스틱", "덴티페어리",
                        ProductCategory.SNACK, TargetSpecies.DOG, 9000, null, "치석 관리 덴탈 간식"),
                Product.create("퓨어비타 동결건조 닭가슴살", "퓨어비타",
                        ProductCategory.SNACK, TargetSpecies.ALL, 15000, null, "동결건조 트릿")
        );

        productRepository.saveAll(products);
        log.info("[SEED] 상품 {}건 시드 완료", products.size());
    }
}
