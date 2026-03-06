package br.com.spring_react.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // rotas de autenticação
                        .requestMatchers(HttpMethod.POST, "/sessions/login").permitAll()
                        .requestMatchers("/sessions/**").authenticated() // para GET /me e POST /logout

                        // endpoints para USERS
                        .requestMatchers(HttpMethod.GET, "/users", "/users/**").permitAll() // tudo que é GET em /users é público
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // cadastro é público
                        .requestMatchers("/users/**").authenticated() // PATCH e DELETE em /users exige login

                        // endpoints para POSTS
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll() // tudo que é GET em /posts é público
                        .requestMatchers("/posts/**").authenticated() // POST, PATCH e DELETE em /posts exige login

                        // endpoints para COMMENTS
                        .requestMatchers(HttpMethod.GET, "/comments/post/**").permitAll() // tudo que é GET em /comments é público
                        .requestMatchers("/comments/**").authenticated() // POST, PATCH e DELETE em /comments exige login

                        // endpoints para LIKES
                        .requestMatchers(HttpMethod.POST, "/likes/post/").authenticated() // é necessário estar logado para curtir posts

                        // endpoints para ACESSAR A DOCUMENTAÇÃO da API
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // outras rotas (fallback)
                        .anyRequest().authenticated()
                )
                // o filtro vem ANTES do filtro padrão de usuário e senha
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
