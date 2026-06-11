package br.com.infromke.blog.user;

import br.com.infromke.blog.shared.ratelimit.RateLimit;
import br.com.infromke.blog.shared.ratelimit.RateLimitType;
import br.com.infromke.blog.user.dto.UserCreateDto;
import br.com.infromke.blog.user.dto.UserDto;
import br.com.infromke.blog.user.dto.UserRoleUpdateDto;
import br.com.infromke.blog.user.dto.UserUpdateDto;
import br.com.infromke.blog.user.internal.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping // GET /users
    @Operation(summary = "Lista todos os usuários registrados", description = "Retorna os dados " +
            "básicos de todos os usuários registrados")
    public ResponseEntity<Object> getAllUsers(@PageableDefault(size = 10, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<User> usersPage = userService.findAllUsers(pageable);

        Page<UserDto> dtoPage = usersPage.map(user ->
                new UserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getSlug(),
                        user.getRole()
                )
        );
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}") // GET /users/id
    @Operation(summary = "Lista o usuário solicitado por ID", description = "Retorna os dados " +
            "básicos do usuário associado ao ID providenciado")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
        User user = userService.findById(id);

        return ResponseEntity.ok(
                new UserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getSlug(),
                        user.getRole()
                )
        );
    }

    @GetMapping("/profile/{userSlug}") // GET /users/profile/userSlug
    @Operation(summary = "Lista o usuário solicitado por slug", description = "Retorna os dados " +
            "básicos do usuário associado ao slug providenciado")
    public ResponseEntity<Object> getUserBySlug(@PathVariable String userSlug) {
        User user = userService.findBySlug(userSlug);

        return ResponseEntity.ok(
                new UserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getSlug(),
                        user.getRole()
                )
        );
    }

    @PostMapping // POST /users
    @RateLimit(type = RateLimitType.SIGNUP)
    @Operation(summary = "Cria um novo usuário", description = "Cria um novo usuário no banco de " +
            "dados com role padrão \"USER\" e avatar gerado a partir de suas iniciais")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto user) {
        User savedUser = userService.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new UserDto(
                                savedUser.getId(),
                                savedUser.getName(),
                                savedUser.getEmail(),
                                savedUser.getAvatar(),
                                savedUser.getSlug(),
                                savedUser.getRole()
                        )
                );
    }

    @PatchMapping("/{id}") // PATCH /users/id
    @Operation(summary = "Atualiza o usuário informado por ID", description = "Atualiza os dados " +
            "básicos do usuário associado ao ID providenciado")
    public ResponseEntity<Object> updateUser(@PathVariable UUID id, HttpServletRequest request,
                                             @Valid @RequestBody UserUpdateDto updateData) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        User updatedUser = userService.updateUser(id, UUID.fromString(userId), updateData);

        return ResponseEntity.ok((Object)
                new UserDto(
                        updatedUser.getId(),
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getAvatar(),
                        updatedUser.getSlug(),
                        updatedUser.getRole()
                )
        );
    }

    @PatchMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // PATCH /users/id/avatar
    @Operation(summary = "Atualiza o avatar do usuário informado por ID", description = "Atualiza" +
            " o avatar do usuário associado ao ID providenciado, excluindo o arquivo binário " +
            "antigo caso o mesmo exista")
    public ResponseEntity<Object> updateAvatar(@PathVariable UUID id,
                                               HttpServletRequest request,
                                               @RequestParam("avatar") MultipartFile file) throws IOException {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        // extrai os bytes e o tipo do arquivo
        byte[] bytes = file.getBytes();
        String contentType = file.getContentType();

        userService.updateAvatar(id, UUID.fromString(userId), bytes, contentType);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role") // PATCH /users/id/role
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualiza o campo \"role\" do usuário informado por ID", description =
            "Promove um usuário de qualquer tipo para USER, AUTHOR ou ADMIN. Apenas " +
                    "administradores podem realizar essa operação")
    public ResponseEntity<Object> updateRole(@PathVariable UUID id,
                                             @Valid @RequestBody UserRoleUpdateDto dto) {
        userService.updateUserRole(id, dto.role());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}") // DELETE /users/id
    @Operation(summary = "Exclui os dados do usuário informado por ID", description = "Deleta o " +
            "usuário associado ao ID providenciado, incluindo dados, avatar, publicações, " +
            "comentários e curtidas")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        userService.deleteUser(id, UUID.fromString(userId));

        return ResponseEntity.noContent().build();
    }
}
