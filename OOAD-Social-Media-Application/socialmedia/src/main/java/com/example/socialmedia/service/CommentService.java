package com.example.socialmedia.service;

import com.example.socialmedia.dto.CommentDTO;
import com.example.socialmedia.dto.CommentRequest;
import com.example.socialmedia.dto.UserDTO;
import com.example.socialmedia.exception.ResourceNotFoundException;
import com.example.socialmedia.exception.UnauthorizedException;
import com.example.socialmedia.model.Comment;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.CommentRepository;
import com.example.socialmedia.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Page<CommentDTO> getCommentsByPostId(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        
        return comments.map(this::mapToDTO);
    }

    public CommentDTO createComment(Long postId, CommentRequest commentRequest, String username) {
        User currentUser = userService.getCurrentUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Validate comment content
        if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        
        // Create new comment
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);
        
        Comment savedComment = commentRepository.save(comment);
        
        return mapToDTO(savedComment);
    }

    public void deleteComment(Long commentId, String username) {
        User currentUser = userService.getCurrentUser(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Check if the current user is the owner of the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        
        // Map user
        UserDTO userDTO = new UserDTO();
        userDTO.setId(comment.getUser().getId());
        userDTO.setUsername(comment.getUser().getUsername());
        userDTO.setProfilePicture(comment.getUser().getProfilePicture());
        commentDTO.setUser(userDTO);
        
        return commentDTO;
    }
}