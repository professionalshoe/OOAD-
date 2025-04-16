// socialmedia-backend/src/main/java/com/example/socialmedia/config/SimpleDataInitializer.java
package com.example.socialmedia.config;

import com.example.socialmedia.model.Post;
import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.PostRepository;
import com.example.socialmedia.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;

@Configuration
public class SimpleDataInitializer {

    @Bean
    public CommandLineRunner initBasicData(UserRepository userRepository, 
                                          PostRepository postRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if we already have data
            if (userRepository.count() > 0) {
                System.out.println("Database already has data, skipping initialization");
                return;
            }
            
            System.out.println("Initializing basic sample data...");
            
            // Create a demo user
            User demoUser = new User();
            demoUser.setUsername("demouser");
            demoUser.setEmail("demo@example.com");
            demoUser.setPassword(passwordEncoder.encode("password"));
            demoUser.setBio("This is a demo user account");
            demoUser.setProfilePicture("https://randomuser.me/api/portraits/men/1.jpg");
            
            userRepository.save(demoUser);
            
            // Create 5 sample posts
            for (int i = 1; i <= 5; i++) {
                Post post = new Post();
                post.setUser(demoUser);
                post.setContent("This is sample post #" + i + " from the demo user.");
                post.setPrivacyLevel(Post.PrivacyLevel.PUBLIC);
                post.setCreatedAt(LocalDateTime.now().minusDays(i));
                
                // Add a sample image URL to some posts
                if (i % 2 == 0) {
                    HashSet<String> mediaUrls = new HashSet<>();
                    mediaUrls.add("https://picsum.photos/id/" + (i * 10) + "/800/600");
                    post.setMediaUrls(mediaUrls);
                }
                
                postRepository.save(post);
            }
            
            System.out.println("Basic sample data initialized successfully!");
        };
    }
}