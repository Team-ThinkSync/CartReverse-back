package com.project.webshopproject.config;

import com.project.webshopproject.security.JwtAuthenticationFilter;
import com.project.webshopproject.security.JwtAuthorizationFilter;
import com.project.webshopproject.security.JwtProvider;
import com.project.webshopproject.security.UserDetailsServiceImpl;
import com.project.webshopproject.user.UserService;
import com.project.webshopproject.user.entity.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserService userService;
//    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;
//    private final JwtLogoutHandler jwtLogoutHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtProvider, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization"); // 클라이언트에서 접근할 수 있게 허용할 헤더 추가
        configuration.addExposedHeader("RefreshToken"); // 클라이언트에서 접근할 수 있게 허용할 헤더 추가

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorize) ->
                authorize
                        // ===== 공통 허용 =====
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/favicon.ico", "/static/**", "/templates/**", "/static/css/**", "/actuator/**", "/error").permitAll()

                        // ===== Public =====
                        .requestMatchers(HttpMethod.POST,
                                "/users/signup",
                                "/auth/login",
                                "/auth/forget-email",
                                "/auth/send-verification-code",
                                "/auth/check-verification-code"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/kakao/callback").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/users/kakao/signup").permitAll()

                        // ===== Users =====
                        .requestMatchers(HttpMethod.DELETE, "/users/resign").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/users/me").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PATCH, "/users/me", "/users/me/password").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority(Grade.ADMIN.getAuthority())

                        // ===== Payments =====
                        .requestMatchers(HttpMethod.POST,
                                "/payments/request",
                                "/payments/cancel",
                                "/payments/confirm"
                        ).hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/payments").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())

                        // ===== Products =====
                        .requestMatchers(HttpMethod.GET,
                                "/products",
                                "/products/{productId}"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/products").hasAuthority(Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PATCH, "/products/{productId}").hasAuthority(Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}").hasAuthority(Grade.ADMIN.getAuthority())

                        // ===== Likes =====
                        .requestMatchers(HttpMethod.POST, "/likes").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority())

                        // ===== Carts =====
                        .requestMatchers(HttpMethod.POST, "/carts").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/carts").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/carts/{cartId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())

                        // ===== Asks =====
                        .requestMatchers(HttpMethod.POST, "/asks").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/asks", "/asks/{askId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PATCH, "/asks/{askId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/asks/{askId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/asks/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/asks/{askId}/response").hasAuthority(Grade.ADMIN.getAuthority())

                        // ===== Reviews =====
                        .requestMatchers(HttpMethod.POST, "/products/{productId}/reviews").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PATCH, "/products/{productId}/reviews/{reviewId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}/reviews/{reviewId}").hasAnyAuthority(Grade.BASIC.getAuthority(), Grade.PREMIUM.getAuthority(), Grade.VIP.getAuthority(), Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/products/{productId}/reviews").permitAll()

                        // ===== Categories =====
                        .requestMatchers(HttpMethod.GET,
                                "/categories",
                                "/categories/{categoryId}/products"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories").hasAuthority(Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PATCH, "/categories/{categoryId}").hasAuthority(Grade.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/categories/{categoryId}").hasAuthority(Grade.ADMIN.getAuthority())
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // ===== Default =====
                        .anyRequest().denyAll()
        );



//        http.logout(logout ->
//                logout.logoutUrl("/v1/auth/logout")
////                        .addLogoutHandler(jwtLogoutHandler)
////                        .logoutSuccessHandler(jwtLogoutSuccessHandler)
//        );

        // 필터 관리
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);


        return http.build();
    }
}