package br.com.infromke.blog.comment;

import br.com.infromke.blog.comment.dto.CommentCreateDto;
import br.com.infromke.blog.comment.dto.CommentDetailsDto;
import br.com.infromke.blog.comment.dto.CommentUpdateDto;
import br.com.infromke.blog.comment.internal.Comment;
import br.com.infromke.blog.comment.internal.CommentMapper;
import br.com.infromke.blog.comment.internal.CommentRepository;
import br.com.infromke.blog.shared.exceptions.ForbiddenActionException;
import br.com.infromke.blog.shared.exceptions.ResourceNotFoundException;
import br.com.infromke.blog.post.PostService;
import br.com.infromke.blog.post.internal.Post;
import br.com.infromke.blog.user.UserService;
import br.com.infromke.blog.user.internal.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostService postService, UserService userService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Page<CommentDetailsDto> findAllByPostId(UUID postId, Pageable pageable) {
        Post post = postService.findEntityById(postId);
        Page<Comment> comments = commentRepository.findAllByPostId(post.getId(), pageable);
        return comments.map(comment -> CommentMapper.toDetailsDto(comment));
    }

    @Transactional(readOnly = true)
    public Comment findEntityById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    @Transactional(readOnly = true)
    public CommentDetailsDto getDetailsById(UUID id) {
        Comment comment = findEntityById(id);
        return CommentMapper.toDetailsDto(comment);
    }

    @Transactional
    public CommentDetailsDto createComment(UUID postId, CommentCreateDto dto, UUID authenticatedUserId) {
        Post post = postService.findEntityById(postId);
        User author = userService.findEntityById(authenticatedUserId);

        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setPost(post);
        comment.setAuthor(author);

        commentRepository.save(comment);
        return getDetailsById(comment.getId());
    }

    @Transactional
    public CommentDetailsDto updateComment(UUID commentId, CommentUpdateDto dto, UUID authenticatedUserId) {
        Comment comment = findEntityById(commentId);

        if (!comment.getAuthor().getId().equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this comment");
        }

        if(dto.content() != null) {
            comment.setContent(dto.content());
        }

        commentRepository.save(comment);
        return getDetailsById(comment.getId());
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID authenticatedUserId) {
        Comment comment = findEntityById(commentId);

        if (!comment.getAuthor().getId().equals(authenticatedUserId)) {
            throw new ForbiddenActionException("You are not authorized to modify this comment");
        }

        commentRepository.delete(comment);
    }
}
