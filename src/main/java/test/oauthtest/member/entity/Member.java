package test.oauthtest.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long kakaoId; // 카카오 고유 번호
    private String nickname;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN 등
}
