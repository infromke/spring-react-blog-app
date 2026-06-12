package br.com.infromke.blog.post;

import br.com.infromke.blog.shared.exceptions.BadRequestException;
import br.com.infromke.blog.shared.ratelimit.RateLimit;
import br.com.infromke.blog.shared.ratelimit.RateLimitType;
import br.com.infromke.blog.post.dto.PostCreateDto;
import br.com.infromke.blog.post.dto.PostDetailsDto;
import br.com.infromke.blog.post.dto.PostUpdateDto;
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

    // GET /posts
    @GetMapping
    @Operation(summary = "Lista todas as publicações criadas", description = "Retorna os dados " +
            "básicos de todas as publicações existentes")
    public ResponseEntity<Object> getAll(@PageableDefault(size = 10, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDetailsDto> posts = postService.findAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    // GET /posts/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Lista a publicação solicitada por ID", description = "Retorna os dados " +
            "básicos da publicação associada ao ID providenciado")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        PostDetailsDto post = postService.getDetailsById(id);
        return ResponseEntity.ok(post);
    }

    // GET posts/slug/postSlug
    @GetMapping("/slug/{postSlug}")
    @Operation(summary = "Lista a publicação solicitada por slug", description = "Retorna os " +
            "dados básicos da publicação associada ao slug providenciado")
    public ResponseEntity<Object> getBySlug(@PathVariable String postSlug) {
        PostDetailsDto post = postService.findBySlug(postSlug);
        return ResponseEntity.ok(post);
    }

    // GET /posts/author/authorSlug
    @GetMapping("/author/{authorSlug}")
    @Operation(summary = "Lista as publicações solicitadas por slug de autor", description =
            "Retorna todas as publicações associada ao slug de autor providenciado")
    public ResponseEntity<Object> getAllByAuthor(@PathVariable String authorSlug,
                                                      @PageableDefault(size = 10, sort =
                                                              "createdAt", direction =
                                                              Sort.Direction.DESC) Pageable pageable) {
        Page<PostDetailsDto> posts = postService.findByAuthor(authorSlug, pageable);
        return ResponseEntity.ok(posts);
    }

    // GET /posts/search?title=...
    @GetMapping("/search")
    @Operation(summary = "Lista as publicações cujo título correspondem ao termo informado",
            description = "Retorna os dados básicos de todas as publicações cujo título " +
                    "correspondem ao termo providenciado")
    public ResponseEntity<Object> getAllByTitle(@RequestParam String title,
                                                     @PageableDefault(size = 10, sort =
                                                             "createdAt", direction =
                                                             Sort.Direction.DESC) Pageable pageable) {
        Page<PostDetailsDto> posts = postService.findByTitle(title, pageable);
        return ResponseEntity.ok(posts);
    }

    // POST /posts
    @PostMapping
    @RateLimit(type = RateLimitType.POST_CREATION)
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @Operation(summary = "Cria uma nova publicação", description = "Vincula uma publicação a um " +
            "autor existente usando o ID do usuário autenticado. Apenas autores e administradores" +
            " podem realizar essa operação")
    public ResponseEntity<Object> create(@Valid @RequestBody PostCreateDto dto,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        PostDetailsDto savedPost = postService.createPost(dto, UUID.fromString(userId));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedPost);
    }

    // PATCH /posts/{id}
    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza a publicação informada por ID", description = "Atualiza os " +
            "dados básicos da publicação associada ao ID providenciado")
    public ResponseEntity<Object> update(@PathVariable("id") UUID postId,
                                             HttpServletRequest request,
                                             @RequestBody PostUpdateDto dto) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        PostDetailsDto updatedPost = postService.updatePost(postId, UUID.fromString(userId), dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(updatedPost);
    }

    // PATCH /posts/{id}/banner
    @PatchMapping(value = "/{id}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualiza o banner da publicação informada por ID", description =
            "Atualiza o banner da publicação associada ao ID providenciado, excluindo o arquivo " +
                    "binário antigo caso o mesmo existe")
    public ResponseEntity<Object> updateBanner(@PathVariable("id") UUID postId,
                                               HttpServletRequest request,
                                               @RequestParam("banner") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Banner file is required and cannot be empty");
        }

        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        // extrai os bytes e o tipo do arquivo
        byte[] bytes = file.getBytes();
        String contentType = file.getContentType();

        postService.updateBanner(postId, UUID.fromString(userId), bytes, contentType);

        return ResponseEntity.noContent().build();
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui os dados da publicação informada por ID", description = "Deleta " +
            "a publicação associada ao ID providenciado, incluindo banner, comentários e curtidas")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID postId,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        postService.deletePost(postId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
