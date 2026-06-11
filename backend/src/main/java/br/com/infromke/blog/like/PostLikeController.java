package br.com.infromke.blog.like;

import br.com.infromke.blog.shared.ratelimit.RateLimit;
import br.com.infromke.blog.shared.ratelimit.RateLimitType;
import br.com.infromke.blog.like.internal.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    // POST /likes/post/{postId}
    @PostMapping("/post/{postId}")
    @RateLimit(type = RateLimitType.LIKE_TOGGLE)
    @Operation(summary = "Alterna curtida em uma publicação", description = "Adiciona ou remove a" +
            " curtida do usuário autenticado em um post.")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID postId,
                                             HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // recuperando o id anexado

        boolean isLiked = postLikeService.toggleLike(postId, UUID.fromString(userId));

        if (isLiked) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.noContent().build();
    }
}
