package test.oauthtest.member.entity;

import jakarta.persistence.*;
import lombok.*;

// 연습에서는 일단 DB에 저장

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId; // 사용자 식별값

    @Column(nullable = false)
    private String token; // 실제 Refresh Token 값


    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
