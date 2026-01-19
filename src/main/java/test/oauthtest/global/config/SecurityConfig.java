package test.oauthtest.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/login/**", "/error").permitAll() // "/error" 추가
                        .anyRequest().authenticated()
                
                );

        return http.build();
    }
    /**
     * WebSecurityCustomizer: 특정 경로를 시큐리티 필터 체인에서 완전히 제외함.
     * permitAll()은 필터를 거치지만 문만 열어주는 것이고, ignoring()은 필터 자체를 타지 않음.
     * 무한 리디렉션이나 403 에러를 방지하는 가장 확실한 방법.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 제외
                .requestMatchers("/h2-console/**") // H2 콘솔 제외
                .requestMatchers("/login/oauth/kakao"); // 카카오 콜백 주소 제외
    }
}