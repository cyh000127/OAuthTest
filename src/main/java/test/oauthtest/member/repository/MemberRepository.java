package test.oauthtest.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.oauthtest.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);
}