package com.petmily.domain.ingredient.entity;

import com.petmily.global.entity.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "aliases", columnDefinition = "TEXT")
    private String aliases;   // JSON (오메가-3/오메가3/Omega-3 등)

    @Column(name = "effects", columnDefinition = "TEXT")
    private String effects;

    @Column(name = "caution_conditions", columnDefinition = "TEXT")
    private String cautionConditions;

    @Builder
    private Ingredient(String name, String aliases, String effects, String cautionConditions) {
        this.name = name;
        this.aliases = aliases;
        this.effects = effects;
        this.cautionConditions = cautionConditions;
    }

    public static Ingredient create(String name, String aliases, String effects, String cautionConditions) {
        return Ingredient.builder()
                .name(name)
                .aliases(aliases)
                .effects(effects)
                .cautionConditions(cautionConditions)
                .build();
    }
}
