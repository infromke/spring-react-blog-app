package br.com.infromke.blog.session;

import br.com.infromke.blog.shared.exceptions.BadRequestException;
import br.com.infromke.blog.shared.services.TokenService;
import br.com.infromke.blog.session.dto.LoginRequestDto;
import br.com.infromke.blog.session.dto.LoginResponse;
import br.com.infromke.blog.user.UserService;
import br.com.infromke.blog.user.internal.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final UserService userService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SessionService(UserService userService, TokenService tokenService,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequestDto data) {
        User user = userService.findByEmailForAuth(data.email());

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = tokenService.generateToken(user);
        return new LoginResponse(token, user);
    }
}
