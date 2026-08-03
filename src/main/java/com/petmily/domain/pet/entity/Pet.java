package com.petmily.domain.pet.entity;

import com.petmily.domain.pet.enums.Gender;
import com.petmily.domain.pet.enums.Species;
import com.petmily.domain.user.entity.User;
import com.petmily.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "pet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false, length = 20)
    private Species species;

    @Column(name = "breed", length = 50)
    private String breed;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "weight", length = 10)
    private String weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Column(name = "is_neutered", nullable = false)
    private boolean isNeutered;

    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Builder
    private Pet(User user, String name, Species species, String breed, LocalDate birthDate,
                String weight, Gender gender, boolean isNeutered, String chronicDiseases,
                String allergies, String profileImageUrl) {
        this.user = user;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.weight = weight;
        this.gender = gender;
        this.isNeutered = isNeutered;
        this.chronicDiseases = chronicDiseases;
        this.allergies = allergies;
        this.profileImageUrl = profileImageUrl;
    }

    public static Pet create(User user, String name, Species species, String breed, LocalDate birthDate,
                             String weight, Gender gender, boolean isNeutered, String chronicDiseases,
                             String allergies, String profileImageUrl) {
        return Pet.builder()
                .user(user)
                .name(name)
                .species(species)
                .breed(breed)
                .birthDate(birthDate)
                .weight(weight)
                .gender(gender)
                .isNeutered(isNeutered)
                .chronicDiseases(chronicDiseases)
                .allergies(allergies)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
