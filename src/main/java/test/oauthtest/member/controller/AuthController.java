package test.oauthtest.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.oauthtest.member.dto.KakaoUserDto;
import test.oauthtest.member.entity.Member;
import test.oauthtest.member.entity.Role;
import test.oauthtest.member.repository.MemberRepository;
import test.oauthtest.member.service.KakaoService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth")
public class AuthController {

    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        // 1, 2, 3단계: 토큰 획득
        String accessToken = kakaoService.getAccessToken(code);

        // 4, 5단계: 사용자 정보 획득
        KakaoUserDto userInfo = kakaoService.getUserInfo(accessToken);

        // 6단계: DB 저장 (H2)
        Member member = memberRepository.findByKakaoId(userInfo.getId())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .kakaoId(userInfo.getId())
                        .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                        .email(userInfo.getKakaoAccount().getEmail())
                        .role(Role.USER)
                        .build()));

        log.info("회원 저장 성공: {}", member.getNickname());

        // 임시로 회원 정보 반환
        return ResponseEntity.ok(member);
    }
}