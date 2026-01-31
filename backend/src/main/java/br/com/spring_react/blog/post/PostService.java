package br.com.spring_react.blog.post;

import br.com.spring_react.blog.post.internal.Post;
import br.com.spring_react.blog.post.internal.PostRepository;
import br.com.spring_react.blog.user.UserService;
import br.com.spring_react.blog.user.internal.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post findById(UUID id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found" +
                "."));
    }

    @Transactional(readOnly = true)
    public Post findBySlug(String slug) {
        return postRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Post not " +
                "found."));
    }

    @Transactional(readOnly = true)
    public List<Post> findByAuthor(String author) {
        return postRepository.findAllByAuthorSlug(author);
    }

    @Transactional(readOnly = true)
    public List<Post> findByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Post createPost(PostCreateDTO data, UUID authorId) {
        User author = userService.findById(authorId);

        if (author == null) {
            throw new RuntimeException("Author not found.");
        }

        Post post = new Post();

        post.setTitle(data.title());
        post.setSummary(data.summary());
        post.setContent(data.content());
        post.setAuthor(author);

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(UUID postId, PostUpdateDTO data) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found."));

        if (data.title() != null) {
            post.setTitle(data.title());
        }

        if (data.summary() != null) {
            post.setSummary(data.summary());
        }

        if (data.content() != null) {
            post.setContent(data.content());
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found.");
        }
        postRepository.deleteById(id);
    }
}
