package test.oauthtest.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.oauthtest.member.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKakaoId(Long KakaoId);

    Optional<RefreshToken> findByToken(String token);


}
