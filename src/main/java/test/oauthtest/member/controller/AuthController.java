package test.oauthtest.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.oauthtest.global.security.JwtTokenProvider;
import test.oauthtest.member.dto.KakaoUserDto;
import test.oauthtest.member.dto.TokenResponse;
import test.oauthtest.member.entity.Member;
import test.oauthtest.member.entity.RefreshToken;
import test.oauthtest.member.entity.Role;
import test.oauthtest.member.repository.MemberRepository;
import test.oauthtest.member.repository.RefreshTokenRepository;
import test.oauthtest.member.service.KakaoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth")
public class AuthController {
    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository; // 추가
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        Member member = memberRepository.findByKakaoId(userInfo.getId())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .kakaoId(userInfo.getId())
                        .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                        .email(userInfo.getKakaoAccount().getEmail())
                        .role(Role.USER).build()));

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getKakaoId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getKakaoId());

        // Refresh Token DB 저장/업데이트
        refreshTokenRepository.findByKakaoId(member.getKakaoId())
                .ifPresentOrElse(
                        t -> t.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .kakaoId(member.getKakaoId())
                                .token(refreshToken).build())
                );

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build());
    }

    // 토큰 재발급 API
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        // 1. 토큰 유효성 검증
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            // 2. DB에 저장된 토큰인지 확인
            return refreshTokenRepository.findByToken(refreshToken)
                    .map(tokenEntity -> {
                        Member member = memberRepository.findByKakaoId(tokenEntity.getKakaoId()).orElseThrow();
                        String newAccessToken = jwtTokenProvider.createAccessToken(member.getKakaoId(), member.getRole());
                        return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}