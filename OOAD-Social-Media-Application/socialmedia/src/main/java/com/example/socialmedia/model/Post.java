package com.example.socialmedia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String content;
    
    @ElementCollection
    @CollectionTable(
        name = "post_media_urls",
        joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "media_url")
    private Set<String> mediaUrls = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel = PrivacyLevel.PUBLIC;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();
    
    public enum PrivacyLevel {
        PUBLIC, FRIENDS, PRIVATE
    }
}