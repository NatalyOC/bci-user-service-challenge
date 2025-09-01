
package com.bci.service.user.config;

import com.bci.service.user.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para la aplicación.
        * <p>
 * Define el filtro de autenticación JWT, las reglas de autorización,
        * y los beans necesarios para la codificación de contraseñas y el manejo de autenticaciones.
        *
        * Requiere que {@link JwtAuthenticationFilter} sea inyectado.
 * @author Nataly Otero Celis
 * @version 1.0
 * @since 2025-08-27
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                  .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(HttpMethod.POST,"/api/users/**").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions().disable()) // Para H2 console
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

