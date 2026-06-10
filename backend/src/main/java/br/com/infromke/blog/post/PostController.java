package br.com.infromke.blog.post;

import br.com.infromke.blog.infra.ratelimit.RateLimit;
import br.com.infromke.blog.infra.ratelimit.RateLimitType;
import br.com.infromke.blog.post.dto.PostCreateDTO;
import br.com.infromke.blog.post.dto.PostDetailsDTO;
import br.com.infromke.blog.post.dto.PostUpdateDTO;
import br.com.infromke.blog.post.internal.Post;
import br.com.infromke.blog.post.internal.PostMapper;
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
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping // GET /posts
    @Operation(summary = "Lista todas as publicações criadas", description = "Retorna os dados " +
            "básicos de todas as publicações existentes")
    public ResponseEntity<Object> getAllPosts(@PageableDefault(size = 10, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> postsPage = postService.findAllPosts(pageable);

        Page<PostDetailsDTO> dtoPage = postsPage.map(
                post -> PostMapper.toDetailsDTO(post)
        );

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}") // GET /posts/{id}
    @Operation(summary = "Lista a publicação solicitada por ID", description = "Retorna os dados " +
            "básicos da publicação associada ao ID providenciado")
    public ResponseEntity<Object> getPostById(@PathVariable UUID id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(PostMapper.toDetailsDTO(post));
    }

    @GetMapping("/slug/{postSlug}") // GET posts/slug/postSlug
    @Operation(summary = "Lista a publicação solicitada por slug", description = "Retorna os " +
            "dados básicos da publicação associada ao slug providenciado")
    public ResponseEntity<Object> getPostBySlug(@PathVariable String postSlug) {
        Post post = postService.findBySlug(postSlug);
        return ResponseEntity.ok(PostMapper.toDetailsDTO(post));
    }

    @GetMapping("/author/{authorSlug}") // GET /posts/author/authorSlug
    @Operation(summary = "Lista as publicações solicitadas por slug de autor", description =
            "Retorna todas as publicações associada ao slug de autor providenciado")
    public ResponseEntity<Object> getAllPostsByAuthor(@PathVariable String authorSlug,
                                                      @PageableDefault(size = 10, sort =
                                                              "createdAt", direction =
                                                              Sort.Direction.DESC) Pageable pageable) {
        Page<Post> postsPage = postService.findByAuthor(authorSlug, pageable);

        Page<PostDetailsDTO> dtoPage = postsPage.map(
                post -> PostMapper.toDetailsDTO(post)
        );

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search") // GET /posts/search?title=...
    @Operation(summary = "Lista as publicações cujo título correspondem ao termo informado",
            description = "Retorna os dados básicos de todas as publicações cujo título " +
                    "correspondem ao termo providenciado")
    public ResponseEntity<Object> getAllPostsByTitle(@RequestParam String title,
                                                     @PageableDefault(size = 10, sort =
                                                             "createdAt", direction =
                                                             Sort.Direction.DESC) Pageable pageable) {
        Page<Post> postsPage = postService.findByTitle(title, pageable);

        Page<PostDetailsDTO> dtoPage = postsPage.map(
                post -> PostMapper.toDetailsDTO(post)
        );

        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping // POST /posts
    @RateLimit(type = RateLimitType.POST_CREATION)
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @Operation(summary = "Cria uma nova publicação", description = "Vincula uma publicação a um " +
            "autor existente usando o ID do usuário autenticado. Apenas autores e administradores" +
            " podem realizar essa operação")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostCreateDTO data,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        Post savedPost = postService.createPost(data, UUID.fromString(userId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PostMapper.toDetailsDTO(savedPost));
    }

    @PatchMapping("/{id}") // PATCH /posts/{id}
    @Operation(summary = "Atualiza a publicação informada por ID", description = "Atualiza os " +
            "dados básicos da publicação associada ao ID providenciado")
    public ResponseEntity<Object> updatePost(@PathVariable("id") UUID postId,
                                             HttpServletRequest request,
                                             @RequestBody PostUpdateDTO updateData) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        Post updatedPost = postService.updatePost(postId, UUID.fromString(userId), updateData);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PostMapper.toDetailsDTO(updatedPost));
    }

    @PatchMapping(value = "/{id}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualiza o banner da publicação informada por ID", description =
            "Atualiza o banner da publicação associada ao ID providenciado, excluindo o arquivo " +
                    "binário antigo caso o mesmo existe")
    public ResponseEntity<Object> updateBanner(@PathVariable("id") UUID postId,
                                               HttpServletRequest request,
                                               @RequestParam("banner") MultipartFile file) throws IOException {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        // extrai os bytes e o tipo do arquivo
        byte[] bytes = file.getBytes();
        String contentType = file.getContentType();

        postService.updateBanner(postId, UUID.fromString(userId), bytes, contentType);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}") // DELETE /posts/{id}
    @Operation(summary = "Exclui os dados da publicação informada por ID", description = "Deleta " +
            "a publicação associada ao ID providenciado, incluindo banner, comentários e curtidas")
    public ResponseEntity<Object> deletePost(@PathVariable("id") UUID postId,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        postService.deletePost(postId, UUID.fromString(userId));

        return ResponseEntity.noContent().build();
    }
}
