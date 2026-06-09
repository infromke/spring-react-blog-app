package br.com.infromke.blog.infra.services;

import br.com.infromke.blog.user.internal.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class TokenService {

    @Value("${api.security.jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("blog-api")
                    .withSubject(user.getId().toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Could not generate JSON token:", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("blog-api")
                    .build()
                    .verify(token)
                    .getSubject(); // retorna o ID do usuário
        } catch (JWTVerificationException exception) {
            return ""; // para token inválido ou expirado
        }
    }

    private Instant genExpirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")) // fuso horário de Brasília
                .plusDays(1) // adiciona 1 dia
                .toInstant();
    }
}
