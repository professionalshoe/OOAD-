// socialmedia-backend/src/main/java/com/example/socialmedia/repository/PostRepository.java
package com.example.socialmedia.repository;

import com.example.socialmedia.model.Post;
import com.example.socialmedia.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Simplified query that just returns all public posts
    @Query("SELECT p FROM Post p WHERE p.privacyLevel = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findPublicPosts(Pageable pageable);
    
    // Original query with proper parameter handling
    @Query("SELECT p FROM Post p WHERE p.privacyLevel = 'PUBLIC' OR " +
           "(p.privacyLevel = 'FRIENDS' AND p.user IN :following) OR " +
           "p.user = :user ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("user") User user, @Param("following") java.util.Set<User> following, Pageable pageable);
}