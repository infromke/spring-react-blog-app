package br.com.infromke.blog.user;

import br.com.infromke.blog.infra.exceptions.BadRequestException;
import br.com.infromke.blog.infra.exceptions.ForbiddenActionException;
import br.com.infromke.blog.infra.exceptions.ResourceAlreadyExistsException;
import br.com.infromke.blog.infra.exceptions.ResourceNotFoundException;
import br.com.infromke.blog.infra.services.MultiPartService;
import br.com.infromke.blog.user.dto.UserCreateDTO;
import br.com.infromke.blog.user.dto.UserUpdateDTO;
import br.com.infromke.blog.user.internal.User;
import br.com.infromke.blog.user.internal.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MultiPartService multiPartService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       MultiPartService multiPartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.multiPartService = multiPartService;
    }

    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "profiles", key = "#userSlug")
    public User findBySlug(String userSlug) {
        return userRepository.findBySlug(userSlug)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByEmailForAuth(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    }

    @Transactional
    public User createUser(UserCreateDTO data) {

        // verifica se o usuário já existe
        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("This e-mail already exists");
        }

        // verifica se as senhas correspondem
        if (!data.password().equals(data.confirmPassword())) {
            throw new BadRequestException("Passwords must match each other");
        }

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));

        return userRepository.save(newUser);
    }

    @Transactional
    @CacheEvict(value = "profiles", allEntries = true)
    public User updateUser(UUID id, UUID authenticatedUserId, UserUpdateDTO data) {
        if (!id.equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // altera nome se "name" não for null
        if (data.name() != null && !data.name().equals(user.getName())) {
            user.setName(data.name());

            // altera o slug do usuário de acordo com a mudança de nome
            String baseSlug = data.name().toLowerCase().replaceAll("[^a-z0-9]", "-");
            String shortId = UUID.randomUUID().toString().split("-")[0];
            user.setSlug(baseSlug + "-" + shortId);
        }

        // altera e-mail se "email" não for null
        if (data.email() != null && !data.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(data.email()).isPresent()) {
                // verifica se o usuário já existe
                throw new ResourceAlreadyExistsException("This e-mail already exists");
            }
            user.setEmail(data.email());
        }

        // altera senha se "password" não for null
        if (data.password() != null) {
            // verifica se as senhas correspondem
            if (!data.password().equals(data.confirmPassword())) {
                throw new BadRequestException("Passwords must match each other");
            }
            user.setPassword(passwordEncoder.encode(data.password()));
        }

        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "profiles", allEntries = true)
    public void updateAvatar(UUID id, UUID authenticatedUserId, byte[] fileBytes,
                             String contentType) {
        if (!id.equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // deleta a imagem antiga se ela existir e não for um link (pra salvar espaço)
        if (user.getAvatar() != null && !user.getAvatar().startsWith("http")) {
            multiPartService.deleteImage("avatars", user.getAvatar());
        }

        // processa, redimensiona e converte a imagem para .webp
        String newFileName = multiPartService.processImage(
                fileBytes,
                contentType,
                "avatars",
                500
        );

        user.setAvatar(newFileName);
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

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // deleta o avatar do usuário se uma imagem física existir
        if (user.getAvatar() != null && !user.getAvatar().startsWith("http")) {
            multiPartService.deleteImage("avatars", user.getAvatar());
        }

        // deleta os banners físicos de todos os posts do usuário caso existam
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> {
                if (post.getBanner() != null && !post.getBanner().startsWith("http")) {
                    multiPartService.deleteImage("banners", post.getBanner());
                }
            });
        }

        userRepository.deleteById(id);
    }
}
