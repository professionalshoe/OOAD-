package com.example.socialmedia.service;

import com.example.socialmedia.dto.UserDTO;
import com.example.socialmedia.exception.ResourceNotFoundException;
import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return mapToDTO(user);
    }

    public void followUser(String currentUsername, Long userToFollowId) {
        User currentUser = getCurrentUser(currentUsername);
        User userToFollow = userRepository.findById(userToFollowId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userToFollowId));
        
        userToFollow.getFollowers().add(currentUser);
        currentUser.getFollowing().add(userToFollow);
        
        userRepository.save(currentUser);
        userRepository.save(userToFollow);
    }

    public void unfollowUser(String currentUsername, Long userToUnfollowId) {
        User currentUser = getCurrentUser(currentUsername);
        User userToUnfollow = userRepository.findById(userToUnfollowId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userToUnfollowId));
        
        userToUnfollow.getFollowers().remove(currentUser);
        currentUser.getFollowing().remove(userToUnfollow);
        
        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setBio(user.getBio());
        userDTO.setFollowersCount(user.getFollowers().size());
        userDTO.setFollowingCount(user.getFollowing().size());
        return userDTO;
    }
}