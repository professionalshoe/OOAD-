package com.example.socialmedia.service;

import com.example.socialmedia.dto.PostDTO;
import com.example.socialmedia.dto.PostRequest;
import com.example.socialmedia.dto.UserDTO;
import com.example.socialmedia.exception.ResourceNotFoundException;
import com.example.socialmedia.exception.UnauthorizedException;
import com.example.socialmedia.model.Like;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.CommentRepository;
import com.example.socialmedia.repository.LikeRepository;
import com.example.socialmedia.repository.PostRepository;

import java.io.IOException;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ImageUploadService imageUploadService;

    public PostService(PostRepository postRepository, LikeRepository likeRepository,
                      CommentRepository commentRepository, UserService userService,
                      ImageUploadService imageUploadService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.imageUploadService= imageUploadService;
    }

    // socialmedia-backend/src/main/java/com/example/socialmedia/service/PostService.java
// Update the getFeed method:

// socialmedia-backend/src/main/java/com/example/socialmedia/service/PostService.java
// Update the getFeed method:

// socialmedia-backend/src/main/java/com/example/socialmedia/service/PostService.java
// Update the getFeed method:
// socialmedia-backend/src/main/java/com/example/socialmedia/service/PostService.java
// Update the getFeed method:

public Page<PostDTO> getFeed(String username, Pageable pageable) {
    try {
        User currentUser = userService.getCurrentUser(username);
        
        // Use the simplified query first to ensure we get some posts
        Page<Post> posts = postRepository.findPublicPosts(pageable);
        
        return posts.map(post -> mapToDTO(post, currentUser));
    } catch (Exception e) {
        // Log the error
        System.err.println("Error fetching feed: " + e.getMessage());
        e.printStackTrace();
        // Return empty page instead of throwing exception
        return Page.empty(pageable);
    }
}
    public PostDTO createPost(String username, PostRequest postRequest) {
        User currentUser = userService.getCurrentUser(username);
        
        // Validate post content
        if (postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        
        // Create new post
        Post post = new Post();
        post.setContent(postRequest.getContent());
        List<String> imageUrls = new ArrayList<>();
        if (postRequest.getMediaUrls() != null) {
            for (String base64Image : postRequest.getMediaUrls()) {
                try {
                    // Upload image and get URL
                    // System.out.println("\n\n\n\n\nReceived Base64 image: " + base64Image.substring(0, Math.min(30, base64Image.length())) + "...");
                    String imageUrl = imageUploadService.uploadImage(base64Image);
                    imageUrls.add(imageUrl);
                    System.out.println("Uploaded image URL: " + imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
                }
                
                
            }
        }
        // System.out.println("Base64 images in mediaUrls: " + postRequest.getMediaUrls());

        post.setMediaUrls(new HashSet<>(imageUrls));

        post.setPrivacyLevel(postRequest.getPrivacyLevel());
        post.setUser(currentUser);
        
        Post savedPost = postRepository.save(post);
        
        return mapToDTO(savedPost, currentUser);
    }

    public PostDTO getPostById(Long postId, String username) {
        User currentUser = userService.getCurrentUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        return mapToDTO(post, currentUser);
    }

    public void deletePost(Long postId, String username) {
        User currentUser = userService.getCurrentUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Check if the current user is the owner of the post
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }
        
        postRepository.delete(post);
    }

    public PostDTO likePost(Long postId, String username) {
        User currentUser = userService.getCurrentUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Check if user already liked the post
        if (likeRepository.existsByUserAndPost(currentUser, post)) {
            throw new IllegalArgumentException("You have already liked this post");
        }
        
        // Create new like
        Like like = new Like();
        like.setUser(currentUser);
        like.setPost(post);
        
        likeRepository.save(like);
        
        return mapToDTO(post, currentUser);
    }

    public PostDTO unlikePost(Long postId, String username) {
        User currentUser = userService.getCurrentUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Find the like
        Like like = likeRepository.findByUserAndPost(currentUser, post)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));
        
        likeRepository.delete(like);
        
        return mapToDTO(post, currentUser);
    }

    private PostDTO mapToDTO(Post post, User currentUser) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setContent(post.getContent());
        postDTO.setMediaUrls(post.getMediaUrls());
        postDTO.setPrivacyLevel(post.getPrivacyLevel());
        postDTO.setCreatedAt(post.getCreatedAt());
        
        // Map user
        UserDTO userDTO = new UserDTO();
        userDTO.setId(post.getUser().getId());
        userDTO.setUsername(post.getUser().getUsername());
        userDTO.setProfilePicture(post.getUser().getProfilePicture());
        postDTO.setUser(userDTO);
        
        // Count likes and comments
        postDTO.setLikesCount(likeRepository.countByPost(post));
        postDTO.setCommentsCount((int) commentRepository.findByPostOrderByCreatedAtDesc(post, Pageable.unpaged()).getTotalElements());
        
        // Check if current user liked the post
        postDTO.setLikedByCurrentUser(likeRepository.existsByUserAndPost(currentUser, post));
        
        return postDTO;
    }
}