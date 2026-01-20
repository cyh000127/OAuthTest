package test.oauthtest.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import test.oauthtest.global.security.JwtAuthenticationFilter;
import test.oauthtest.global.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/login/**", "/error").permitAll() // "/error" 추가
                        .anyRequest().authenticated()
                ).
                // JWT 필터 추가
                addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    /**
     * WebSecurityCustomizer: 특정 경로를 시큐리티 필터 체인에서 완전히 제외함.
     * 유저 로그인이 유지되지 않아 무한 리디렉션이 발생 -> 해결하기 위해 ignoring 진행
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 제외
                .requestMatchers("/h2-console/**") // H2 콘솔 제외
                .requestMatchers("/login/oauth/kakao"); // 카카오 콜백 주소 제외
    }
}