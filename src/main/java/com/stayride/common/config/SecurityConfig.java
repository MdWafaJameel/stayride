package com.stayride.common.config;

import com.stayride.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/users"
                        ).permitAll()

                        .requestMatchers("/api/users/hotel-admins")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/bookings/**"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Everyone authenticated can view hotels
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET, "/api/hotels", "/api/hotels/**" )
                        .hasAnyRole( "CUSTOMER", "HOTEL_ADMIN", "ADMIN" )

                        // ========================= // HOTELS - WRITE // ========================= // Only HOTEL_ADMIN and ADMIN can create hotels
                                // Create hotel
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/hotels"
                                ).hasAnyRole(
                                        "HOTEL_ADMIN",
                                        "ADMIN"
                                )

                                // Update hotel
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/hotels/*"
                                ).hasAnyRole(
                                        "HOTEL_ADMIN",
                                        "ADMIN"
                                )


                        // Only HOTEL_ADMIN and ADMIN can create rooms
                        .requestMatchers( org.springframework.http.HttpMethod.POST, "/api/hotels/*/rooms"
                        ).hasAnyRole( "HOTEL_ADMIN", "ADMIN" )

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}