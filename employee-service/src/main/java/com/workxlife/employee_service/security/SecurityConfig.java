package com.workxlife.employee_service.security;

import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // ✅ Allow iframe/embed
                )
                .authorizeHttpRequests(auth -> auth
                        //  Allow access to static image and resume files
                        .requestMatchers("/uploads/**").permitAll()

                        //  Allow public employee registration
                        .requestMatchers(HttpMethod.POST, "/api/employees").permitAll()

                        //  Allow internal service/employee access by email
                        .requestMatchers(HttpMethod.GET, "/api/employees/email/**")
                        .hasAnyAuthority("ROLE_INTERNAL_SERVICE", "ROLE_EMPLOYEE")

                        //  Everything else must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
