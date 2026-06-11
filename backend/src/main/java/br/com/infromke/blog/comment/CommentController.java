package br.com.infromke.blog.comment;

import br.com.infromke.blog.comment.dto.CommentCreateDto;
import br.com.infromke.blog.comment.dto.CommentDetailsDto;
import br.com.infromke.blog.comment.dto.CommentUpdateDto;
import br.com.infromke.blog.shared.ratelimit.RateLimit;
import br.com.infromke.blog.shared.ratelimit.RateLimitType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /comments/post/{postId}
    @GetMapping("/post/{postId}")
    @Operation(summary = "Lista todos os comentários em uma publicação", description = "Retorna " +
            "todos os comentários associados ao ID da publicação informada")
    public ResponseEntity<Object> getAllComments(@PathVariable UUID postId,
                                                 @PageableDefault(size = 10, sort = "createdAt",
                                                         direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentDetailsDto> comments = commentService.findAllByPostId(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    // POST /comments/post/{postId}
    @PostMapping("/post/{postId}")
    @RateLimit(type = RateLimitType.COMMENT_CREATION)
    @Operation(summary = "Cria um novo comentário", description = "Vincula um comentário a um " +
            "post existente usando o ID do usuário autenticado e o ID da publicação")
    public ResponseEntity<Object> createComment(@PathVariable UUID postId,
                                                HttpServletRequest request,
                                                @Valid @RequestBody CommentCreateDto dto) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        CommentDetailsDto savedComment = commentService.createComment(postId, dto, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // PATCH /comments/{id}
    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza o comentário informado por ID", description = "Atualiza o " +
            "conteúdo do comentário associado ao ID providenciado")
    public ResponseEntity<Object> updateComment(@PathVariable("id") UUID commentId,
                                                HttpServletRequest request,
                                                @Valid @RequestBody CommentUpdateDto dto) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        CommentDetailsDto updatedComment = commentService.updateComment(commentId, dto, UUID.fromString(userId));
        return ResponseEntity.ok(updatedComment);
    }

    // DELETE /comments/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui o comentário informado por ID", description = "Deleta o " +
            "comentário associado ao ID providenciado")
    public ResponseEntity<Object> deleteComment(@PathVariable("id") UUID commentId,
                                                HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado
        commentService.deleteComment(commentId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
