package br.com.infromke.blog.user;

import br.com.infromke.blog.shared.exceptions.BadRequestException;
import br.com.infromke.blog.shared.exceptions.ForbiddenActionException;
import br.com.infromke.blog.shared.exceptions.ResourceAlreadyExistsException;
import br.com.infromke.blog.shared.exceptions.ResourceNotFoundException;
import br.com.infromke.blog.shared.helpers.ImageStorageHelper;
import br.com.infromke.blog.shared.utils.SlugGenerator;
import br.com.infromke.blog.user.dto.UserCreateDto;
import br.com.infromke.blog.user.dto.UserDto;
import br.com.infromke.blog.user.dto.UserUpdateDto;
import br.com.infromke.blog.user.internal.User;
import br.com.infromke.blog.user.internal.UserMapper;
import br.com.infromke.blog.user.internal.UserRepository;
import br.com.infromke.blog.user.internal.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageStorageHelper imageStorageHelper;

    public UserService(UserRepository userRepository, UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder,
                       ImageStorageHelper imageStorageHelper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.imageStorageHelper = imageStorageHelper;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> userMapper.toDto(user));
    }

    @Transactional(readOnly = true)
    public User findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDto getSummaryById(UUID id) {
        User user = findEntityById(id);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "profiles", key = "#userSlug")
    public UserDto findBySlug(String userSlug) {
        User user = userRepository.findBySlug(userSlug)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return getSummaryById(user.getId());
    }

    public User findByEmailForAuth(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    }

    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        // verifica se o usuário já existe
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("This e-mail already exists");
        }

        // verifica se as senhas correspondem
        if (!dto.password().equals(dto.confirmPassword())) {
            throw new BadRequestException("Passwords must match each other");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(newUser);
        return getSummaryById(newUser.getId());
    }

    @Transactional
    @CacheEvict(value = "profiles", allEntries = true)
    public UserDto updateUser(UUID id, UUID authenticatedUserId, UserUpdateDto dto) {
        if (!id.equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this account");
        }

        User user = findEntityById(id);

        // altera nome se "name" não for null e também altera o slug
        if (dto.name() != null && !dto.name().equals(user.getName())) {
            user.setName(dto.name());
            user.setSlug(SlugGenerator.generateForUser(dto.name()));
        }

        // altera e-mail se "email" não for null e se o e-mail estiver disponível
        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(dto.email()).isPresent()) {
                throw new ResourceAlreadyExistsException("This e-mail already exists");
            }
            user.setEmail(dto.email());
        }

        // altera senha se "password" não for null
        if (dto.password() != null) {
            if (!dto.password().equals(dto.confirmPassword())) {
                throw new BadRequestException("Passwords must match each other");
            }
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        userRepository.save(user);
        return getSummaryById(id);
    }

    @Transactional
    @CacheEvict(value = "profiles", allEntries = true)
    public void updateAvatar(UUID id, UUID authenticatedUserId, byte[] fileBytes,
                             String contentType) {
        if (!id.equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this account");
        }

        User user = findEntityById(id);

        // deleta a imagem antiga se ela existir e não for um link (pra salvar espaço)
        if (user.getAvatar() != null && !user.getAvatar().startsWith("http")) {
            imageStorageHelper.deleteImage("avatars", user.getAvatar());
        }

        // processa, redimensiona e converte a imagem para .webp
        String newFileName = imageStorageHelper.processImage(
                fileBytes,
                contentType,
                "avatars",
                500
        );

        user.setAvatar(newFileName);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(UUID id, UserRole newRole) {
        User user = findEntityById(id);
        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "profiles", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true)
    })
    public void deleteUser(UUID id, UUID authenticatedUserId) {
        if (!id.equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this account");
        }

        User user = findEntityById(id);

        // deleta o avatar do usuário se uma imagem física existir
        if (user.getAvatar() != null && !user.getAvatar().startsWith("http")) {
            imageStorageHelper.deleteImage("avatars", user.getAvatar());
        }

        // deleta os banners físicos de todos os posts do usuário caso existam
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> {
                if (post.getBanner() != null && !post.getBanner().startsWith("http")) {
                    imageStorageHelper.deleteImage("banners", post.getBanner());
                }
            });
        }

        userRepository.deleteById(id);
    }
}
