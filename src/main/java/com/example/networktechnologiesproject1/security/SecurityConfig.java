package com.example.networktechnologiesproject1.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for security settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String key;

    /**
     * Defines a PasswordEncoder bean for encoding passwords.
     * @return BCryptPasswordEncoder object for password encoding.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines an AuthenticationManager bean for managing authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationManagerBuilder auth) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    /**
     * Defines a SecurityFilterChain bean for setting up security filters.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll())
                        .requestMatchers("/register", "/login").permitAll() // Permit all for login and register
                        // Swagger permissions
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/swagger-resources", "/v3/api-docs/**", "/proxy/**").permitAll()
                        // Reader permissions (ROLE_USER and ROLE_STAFF)
                        .requestMatchers(HttpMethod.POST, "/loan/add").hasAnyRole("USER", "STAFF") // Borrow a book
                        .requestMatchers(HttpMethod.DELETE, "/loan/delete/*").hasAnyRole("USER", "STAFF") // Return a book
                        .requestMatchers(HttpMethod.POST, "/review/add").hasAnyRole("USER", "STAFF") // Add a review (BONUS)
                        .requestMatchers(HttpMethod.GET, "/loan/getAll", "/loan/*").hasAnyRole("USER", "STAFF") // View Loan History
                        // Librarian permissions (ROLE_STAFF)
                        .requestMatchers(HttpMethod.POST, "/bookDetail/add").hasRole("STAFF") // Add book details
                        .requestMatchers(HttpMethod.PUT, "/bookDetail/update/*").hasRole("STAFF") // Update book details
                        .requestMatchers(HttpMethod.DELETE, "/bookDetail/delete/*").hasRole("STAFF") // Delete book details
                        .requestMatchers(HttpMethod.GET, "/user/getAll", "/user/*").hasRole("STAFF") // Manage User Accounts
                        .requestMatchers(HttpMethod.POST, "/user/add").hasRole("STAFF") // Add a user
                        .requestMatchers(HttpMethod.PUT, "/user/update/*").hasRole("STAFF") // Update user
                        .requestMatchers(HttpMethod.DELETE, "/user/delete/*").hasRole("STAFF") // Delete user
                        // .requestMatchers(HttpMethod.GET, "/reports/**").hasRole("STAFF") // Generate reports (BONUS)
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JWTTokenFilter(key), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
