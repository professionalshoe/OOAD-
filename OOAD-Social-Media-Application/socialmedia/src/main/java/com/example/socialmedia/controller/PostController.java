package com.example.socialmedia.controller;

import com.example.socialmedia.dto.PostDTO;
import com.example.socialmedia.dto.PostRequest;
import com.example.socialmedia.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getFeed(userDetails.getUsername(), pageable));
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(userDetails.getUsername(), postRequest));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPostById(postId, userDetails.getUsername()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostDTO> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.likePost(postId, userDetails.getUsername()));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<PostDTO> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.unlikePost(postId, userDetails.getUsername()));
    }
}