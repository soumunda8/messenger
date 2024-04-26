package com.server.commmons.security.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${cors.client.url}")
    private String clientUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.addAllowedOrigin(clientUrl);
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);
                    cors.configurationSource(source);
                })
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화 (상태 저장하지 않는 REST API에 적합)
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.requestMatchers("/user/**").authenticated();
                    authorizeRequests.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER");
                    authorizeRequests.requestMatchers("/admin/**").hasRole("ADMIN");
                    authorizeRequests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorizeRequests.anyRequest().permitAll();
                })
                .formLogin((formLogin) -> {
                    formLogin.loginPage("/login");
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout");  // 로그아웃 처리 URL
                    logout.logoutSuccessHandler(new LogoutSuccessHandler() {
                        @Override
                        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            PrintWriter out = response.getWriter();
                            out.println("{\"message\":\"Successfully logged out\"}");
                            out.flush();
                        }
                    });  // 로그아웃 성공 후 리디렉션 페이지
                    logout.deleteCookies("JSESSIONID");  // 로그아웃 시 쿠키 삭제
                    logout.clearAuthentication(true);  // 인증 정보 클리어
                    logout.invalidateHttpSession(true);  // HTTP 세션 무효화
                })
                .build();
    }

}