package com.example.socialmedia.repository;

import com.example.socialmedia.model.Like;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
    boolean existsByUserAndPost(User user, Post post);
}