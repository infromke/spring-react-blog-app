package br.com.infromke.blog.session;

import br.com.infromke.blog.shared.ratelimit.RateLimit;
import br.com.infromke.blog.shared.ratelimit.RateLimitType;
import br.com.infromke.blog.session.dto.LoginRequestDto;
import br.com.infromke.blog.session.dto.LoginResponse;
import br.com.infromke.blog.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // GET /sessions/me
    @GetMapping("/me")
    @Operation(summary = "Lista o usuário autenticado", description = "Retorna os dados básicos " +
            "do usuário atualmente autenticado")
    public ResponseEntity<Object> status(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recupera o ID do usuário
        UserDto user = sessionService.showStatus(UUID.fromString(userId));
        return ResponseEntity.ok(user);
    }

    // POST /sessions/login
    @PostMapping("/login")
    @RateLimit(type = RateLimitType.LOGIN)
    @SecurityRequirements(value = {})
    @Operation(summary = "Realiza o login de um usuário", description = "Cria uma nova sessão e " +
            "atribui um token de acesso (JWT) a um cookie httpOnly")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto dto,
                                        HttpServletResponse response) {
        LoginResponse loginData = sessionService.authenticate(dto);

        ResponseCookie cookie = ResponseCookie.from("accessToken", loginData.token())
                .httpOnly(true)
                .secure(false) // mudar para true em produção
                .path("/")
                .maxAge(60 * 60 * 24) // 1 dia
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginData.user());
    }

    // POST /sessions/logout
    @PostMapping("/logout")
    @Operation(summary = "Realiza o logout", description = "Destrói a sessão e " +
            "remove o cookie httpOnly do navegador")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) // true em prod
                .path("/")
                .maxAge(0) // destrói o cookie
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
