// src/api/index.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth API
export const register = (userData) => api.post('/auth/register', userData);
export const login = (credentials) => api.post('/auth/login', credentials);

// Posts API
export const getFeed = (page = 0, size = 10) => 
  api.get(`/posts?page=${page}&size=${size}&sort=createdAt,desc`);
export const createPost = (postData) => api.post('/posts', postData);
export const getPost = (postId) => api.get(`/posts/${postId}`);
export const deletePost = (postId) => api.delete(`/posts/${postId}`);
export const likePost = (postId) => api.post(`/posts/${postId}/like`);
export const unlikePost = (postId) => api.delete(`/posts/${postId}/like`);

// Comments API
export const getComments = (postId, page = 0, size = 10) => 
  api.get(`/posts/${postId}/comments?page=${page}&size=${size}&sort=createdAt,desc`);
export const createComment = (postId, commentData) => 
  api.post(`/posts/${postId}/comments`, commentData);
export const deleteComment = (postId, commentId) => 
  api.delete(`/posts/${postId}/comments/${commentId}`);

// User API
export const getUserProfile = (userId) => api.get(`/users/${userId}`);
export const followUser = (userId) => api.post(`/users/${userId}/follow`);
export const unfollowUser = (userId) => api.post(`/users/${userId}/unfollow`);

export default api;