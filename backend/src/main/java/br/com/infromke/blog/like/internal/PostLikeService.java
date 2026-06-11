package br.com.infromke.blog.like.internal;

import br.com.infromke.blog.post.PostService;
import br.com.infromke.blog.post.internal.Post;
import br.com.infromke.blog.user.UserService;
import br.com.infromke.blog.user.internal.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final UserService userService;

    public PostLikeService(PostLikeRepository postLikeRepository, PostService postService,
                           UserService userService) {
        this.postLikeRepository = postLikeRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Transactional
    public boolean toggleLike(UUID postId, UUID userId) {
        Post post = postService.findEntityById(postId);

        // verifica se um like já foi dado pelo usuário no post
        var existingLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        // remove o like se ele já existir
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return false;
        }

        // cria o like caso ele não exista
        User user = userService.findEntityById(userId);

        var newLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(newLike);
        return true;
    }
}
