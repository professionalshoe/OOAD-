package com.example.socialmedia.controller;

import com.example.socialmedia.dto.CommentDTO;
import com.example.socialmedia.dto.CommentRequest;
import com.example.socialmedia.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.createComment(postId, commentRequest, userDetails.getUsername()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}