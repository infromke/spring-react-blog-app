package br.com.spring_react.blog.post;

import br.com.spring_react.blog.post.internal.Post;
import br.com.spring_react.blog.post.internal.PostMapper;
import br.com.spring_react.blog.user.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping // GET /posts
    public ResponseEntity<Object> getAllPosts() {
        List<Post> posts = postService.findAllPosts();

        if (posts.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("There are no recorded posts."));
        }

        List<PostDetailsDTO> dtos =
                posts.stream().map(post -> PostMapper.toDetailsDTO(post)).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}") // GET /posts/{id}
    public ResponseEntity<Object> getPostById(@PathVariable UUID id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(PostMapper.toDetailsDTO(post));
    }

    @GetMapping("/slug/{postSlug}") // GET posts/slug/postSlug
    public ResponseEntity<Object> getPostBySlug(@PathVariable String postSlug) {
        Post post = postService.findBySlug(postSlug);
        return ResponseEntity.ok(PostMapper.toDetailsDTO(post));
    }

    @GetMapping("/author/{authorSlug}") // GET /posts/author/authorSlug
    public ResponseEntity<Object> getAllPostsByAuthor(@PathVariable String authorSlug) {
        List<Post> posts = postService.findByAuthor(authorSlug);

        if (posts.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("There are no recorded posts from this " +
                    "author yet."));
        }

        List<PostDetailsDTO> dtos =
                posts.stream().map(post -> PostMapper.toDetailsDTO(post)).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search") // GET /posts/search?title=...
    public ResponseEntity<Object> getAllPostsByTitle(@RequestParam String title) {
        List<Post> posts = postService.findByTitle(title);

        if (posts.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("There are no matching results to this " +
                    "search."));
        }

        List<PostDetailsDTO> dtos =
                posts.stream().map(post -> PostMapper.toDetailsDTO(post)).toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping // POST /posts
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostCreateDTO data,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(
                    "User " +
                            "not authenticated."));
        }

        Post savedPost = postService.createPost(data, UUID.fromString(userId));

        return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDetailsDTO(savedPost));
    }

    @PatchMapping("/{id}") // PATCH /posts/{id}
    public ResponseEntity<Object> updatePost(@PathVariable UUID id, HttpServletRequest request,
                                             @RequestBody PostUpdateDTO updateData) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(
                    "User not authenticated."));
        }

        Post updatedPost = postService.updatePost(id, UUID.fromString(userId), updateData);

        return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDetailsDTO(updatedPost));
    }

    @DeleteMapping("/{id}") // DELETE /posts/{id}
    public ResponseEntity<Object> deletePost(@PathVariable UUID id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        postService.deletePost(id, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
