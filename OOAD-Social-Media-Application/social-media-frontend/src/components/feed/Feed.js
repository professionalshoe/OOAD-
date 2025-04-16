// src/components/feed/Feed.js
import React, { useState, useEffect } from 'react';
import { getFeed } from '../../api';
import Post from './Post';
import CreatePost from './CreatePost';
import { Box, CircularProgress, Typography, Button } from '@mui/material';
import './Feed.css'; // Import the CSS file

const Feed = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  // social-media-frontend/src/components/feed/Feed.js
// Update the fetchPosts function:

const fetchPosts = async (pageNum = 0) => {
  try {
    setLoading(true);
    setError('');
    
    const response = await getFeed(pageNum);
    const newPosts = response.data.content || [];
    
    if (pageNum === 0) {
      setPosts(newPosts);
    } else {
      setPosts((prevPosts) => [...prevPosts, ...newPosts]);
    }
    
    setHasMore(!response.data.last);
    setPage(pageNum);
  } catch (err) {
    console.error('Error fetching posts:', err);
    setError('Failed to load posts. Please try again later.');
    // Don't set empty posts array on error to keep any existing posts
  } finally {
    setLoading(false);
  }
};

  useEffect(() => {
    fetchPosts();
  }, []);

  const handleLoadMore = () => {
    fetchPosts(page + 1);
  };

  const handlePostCreated = (newPost) => {
    setPosts([newPost, ...posts]);
  };

  const handlePostDeleted = (postId) => {
    setPosts(posts.filter(post => post.id !== postId));
  };

  const handlePostLiked = (updatedPost) => {
    setPosts(posts.map(post => post.id === updatedPost.id ? updatedPost : post));
  };

  return (
    <Box>
      <CreatePost onPostCreated={handlePostCreated} />
      
      {error && (
        <Typography color="error" sx={{ mt: 2 }}>
          {error}
        </Typography>
      )}
      
      {posts.length === 0 && !loading ? (
        <Typography sx={{ mt: 4, textAlign: 'center' }}>
          No posts yet. Be the first to post!
        </Typography>
      ) : (
        <Box sx={{ mt: 4 }}>
          {posts.map((post) => (
            <Post 
              key={post.id} 
              post={post} 
              onPostDeleted={handlePostDeleted}
              onPostLiked={handlePostLiked}
            />
          ))}
          
          {hasMore && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2, mb: 4 }}>
              <Button 
                variant="outlined" 
                onClick={handleLoadMore} 
                disabled={loading}
              >
                {loading ? 'Loading...' : 'Load More'}
              </Button>
            </Box>
          )}
          
          {loading && page === 0 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <CircularProgress />
            </Box>
          )}
        </Box>
      )}
    </Box>
  );
};

export default Feed;