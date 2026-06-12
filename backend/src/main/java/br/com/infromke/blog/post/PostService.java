package br.com.infromke.blog.post;

import br.com.infromke.blog.post.dto.PostDetailsDto;
import br.com.infromke.blog.post.internal.PostMapper;
import br.com.infromke.blog.shared.exceptions.ForbiddenActionException;
import br.com.infromke.blog.shared.exceptions.ResourceNotFoundException;
import br.com.infromke.blog.shared.helpers.ImageStorageHelper;
import br.com.infromke.blog.post.dto.PostCreateDto;
import br.com.infromke.blog.post.dto.PostUpdateDto;
import br.com.infromke.blog.post.internal.Post;
import br.com.infromke.blog.post.internal.PostRepository;
import br.com.infromke.blog.user.UserService;
import br.com.infromke.blog.user.internal.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final ImageStorageHelper imageStorageHelper;

    public PostService(PostRepository postRepository, PostMapper postMapper,
                       UserService userService, ImageStorageHelper imageStorageHelper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userService = userService;
        this.imageStorageHelper = imageStorageHelper;
    }

    @Transactional(readOnly = true)
    public Page<PostDetailsDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> postMapper.toDetailsDto(post));
    }

    @Transactional(readOnly = true)
    public Post findEntityById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    @Transactional(readOnly = true)
    public PostDetailsDto getDetailsById(UUID id) {
        Post post = findEntityById(id);
        return postMapper.toDetailsDto(post);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#postSlug")
    public PostDetailsDto findBySlug(String postSlug) {
        Post post = postRepository.findBySlug(postSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return postMapper.toDetailsDto(post);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "authors", key = "#authorSlug")
    public Page<PostDetailsDto> findByAuthor(String authorSlug, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByAuthorSlug(authorSlug, pageable);
        return posts.map(post -> postMapper.toDetailsDto(post));
    }

    @Transactional(readOnly = true)
    public Page<PostDetailsDto> findByTitle(String title, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingIgnoreCase(title, pageable);
        return posts.map(post -> postMapper.toDetailsDto(post));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "authors", allEntries = true)
    })
    public PostDetailsDto createPost(PostCreateDto dto, UUID authorId) {
        User author = userService.findEntityById(authorId);

        Post post = new Post();
        post.setTitle(dto.title());
        post.setSummary(dto.summary());
        post.setContent(dto.content());
        post.setAuthor(author);

        postRepository.save(post);
        return getDetailsById(post.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "authors", allEntries = true)
    })
    public PostDetailsDto updatePost(UUID postId, UUID authenticatedUserId, PostUpdateDto dto) {
        Post post = findEntityById(postId);

        if (!post.getAuthor().getId().equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this post");
        }

        if (dto.title() != null) {
            post.setTitle(dto.title());
        }

        if (dto.summary() != null) {
            post.setSummary(dto.summary());
        }

        if (dto.content() != null) {
            post.setContent(dto.content());
        }

        postRepository.save(post);
        return getDetailsById(post.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "authors", allEntries = true)
    })
    public void updateBanner(UUID id, UUID authenticatedUserId, byte[] fileBytes,
                             String contentType) {
        Post post = findEntityById(id);

        if (!post.getAuthor().getId().equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this post");
        }

        // deleta a imagem antiga se ela existir e não for um link (pra salvar espaço)
        if (post.getBanner() != null && !post.getBanner().startsWith("http")) {
            imageStorageHelper.deleteImage("banners", post.getBanner());
        }

        // processa, redimensiona e converte a imagem para .webp
        String newFileName = imageStorageHelper.processImage(
                fileBytes,
                contentType,
                "banners",
                1200
        );

        post.setBanner(newFileName);
        postRepository.save(post);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "authors", allEntries = true)
    })
    public void deletePost(UUID id, UUID authenticatedUserId) {
        Post post = findEntityById(id);

        if (!post.getAuthor().getId().equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this post");
        }

        // deleta a imagem antiga se ela existir e não for um link (pra salvar espaço)
        if (post.getBanner() != null && !post.getBanner().startsWith("http")) {
            imageStorageHelper.deleteImage("banners", post.getBanner());
        }

        postRepository.deleteById(id);
    }
}
