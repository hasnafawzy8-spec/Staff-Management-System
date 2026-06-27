package com.stuffmanagement.staffmansys.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use delegating encoder so stored passwords like "{noop}..." are supported
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for simplicity (use CSRF tokens in production)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Profile pages (fix for Finance role)
                        .requestMatchers("/profile", "/my-profile").hasAnyRole("EMPLOYEE", "HR", "SUPERUSER", "FINANCE")

                        // Employee self-service
                        .requestMatchers("/account/**").hasAnyRole("EMPLOYEE", "HR", "SUPERUSER", "FINANCE")

                        // Password management
                        .requestMatchers("/password/reset").hasAnyRole("EMPLOYEE", "HR", "SUPERUSER", "FINANCE")
                        .requestMatchers("/password/admin/**").hasAnyRole("HR", "SUPERUSER", "FINANCE")

                        // Department management (admin only)
                        .requestMatchers("/admin/departments/**").hasAnyRole("HR", "SUPERUSER")

                        // Employee management (admin only)
                        .requestMatchers("/admin/employees/**").hasAnyRole("HR", "SUPERUSER")

                        // Attendance
                        .requestMatchers("/attendance/**").hasAnyRole("EMPLOYEE", "HR", "SUPERUSER", "FINANCE")
                        .requestMatchers("/admin/attendance/**").hasAnyRole("HR", "SUPERUSER")

                        // Leaves
                        .requestMatchers("/leaves/**").hasAnyRole("EMPLOYEE", "HR", "SUPERUSER", "FINANCE")
                        .requestMatchers("/admin/leaves/**").hasAnyRole("HR", "SUPERUSER")

                        // Payroll
                        .requestMatchers(HttpMethod.GET, "/admin/payrolls/**").hasAnyRole("FINANCE", "HR", "SUPERUSER")
                        .requestMatchers(HttpMethod.POST, "/admin/payrolls/**").hasAnyRole("FINANCE", "HR", "SUPERUSER")

                        // Performance or generic admin sections
                        .requestMatchers("/admin/**").hasAnyRole("HR", "SUPERUSER", "FINANCE")

                        // Superuser-only
                        .requestMatchers("/super/**").hasRole("SUPERUSER")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )

                // Login settings
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/dashboard", true)
                )

                // Logout defaults
                .logout(Customizer.withDefaults());

        return http.build();
    }
}
