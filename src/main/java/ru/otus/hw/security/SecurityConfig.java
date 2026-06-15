package ru.otus.hw.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final WebAccessDeniedHandler webAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/book").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/book").permitAll()
                        .requestMatchers(HttpMethod.GET,"/book/\\d+/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/book/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/book").hasRole("EDITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/book/**").hasRole("EDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/book/**").hasRole("EDITOR")
                        .requestMatchers("/book/edit").hasRole("EDITOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .loginProcessingUrl("/process-login")
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(h -> h.accessDeniedHandler(webAccessDeniedHandler));
        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("EDITOR").implies("USER")
                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
