package br.com.spring_react.blog.session;

import br.com.spring_react.blog.user.MessageResponse;
import br.com.spring_react.blog.user.UserDTO;
import br.com.spring_react.blog.user.UserService;
import br.com.spring_react.blog.user.internal.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Object> me(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recupera o ID do usuário

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("User " +
                    "not authenticated."));
        }

        // busca pelo usuário
        User user = userService.findById(UUID.fromString(userId));

        // retorna dados do usuário logado
        return ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatar(),
                user.getSlug(),
                user.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO data,
                                        HttpServletResponse response) {
        try {
            String token = sessionService.authenticate(data);

            ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                    .httpOnly(true)
                    .secure(false) // mudar para true em produção
                    .path("/")
                    .maxAge(2 * 60 * 60) // 2 horas
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new MessageResponse("Logged in."));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) // true em prod
                .path("/")
                .maxAge(0) // destrói o cookie
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logged out."));
    }
}
