package com.petmily.domain.user.entity;


import com.petmily.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column
    private LocalDateTime expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RefreshToken(User user,String newToken, LocalDateTime newExpireAt) {
        this.user = user;
        this.token = newToken;
        this.expireAt = newExpireAt;
    }

    public void rotate(String token, LocalDateTime expireAt) {
        this.token = token;
        this.expireAt = expireAt;
    }

}
