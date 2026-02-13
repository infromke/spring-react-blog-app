package br.com.spring_react.blog.comment;

import br.com.spring_react.blog.comment.internal.Comment;
import br.com.spring_react.blog.comment.internal.CommentMapper;
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

    @GetMapping("/post/{postId}")
    public ResponseEntity<Object> getAllComments(@PathVariable UUID postId,
                                                 @PageableDefault(size = 10, sort = "createdAt",
                                                         direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Comment> commentsPage = commentService.findAllByPostId(postId, pageable);

        Page<CommentDetailsDTO> dtoPage =
                commentsPage.map(comment -> CommentMapper.toDetailsDTO(comment));

        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<Object> createComment(@PathVariable UUID postId,
                                                HttpServletRequest request,
                                                @Valid @RequestBody CommentCreateDTO data) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        Comment savedComment = commentService.createComment(postId, data, UUID.fromString(userId));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentMapper.toDetailsDTO(savedComment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateComment(@PathVariable("id") UUID commentId,
                                                HttpServletRequest request,
                                                @Valid @RequestBody CommentUpdateDTO updateData) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        Comment updatedComment = commentService.updateComment(commentId, updateData,
                UUID.fromString(userId));

        return ResponseEntity.ok(CommentMapper.toDetailsDTO(updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable("id") UUID commentId,
                                                HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        commentService.deleteComment(commentId, UUID.fromString(userId));

        return ResponseEntity.noContent().build();
    }
}
