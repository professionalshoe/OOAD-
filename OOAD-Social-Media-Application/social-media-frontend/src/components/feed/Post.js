// src/components/feed/Post.js
import React, { useState, useContext } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { likePost, unlikePost, deletePost } from '../../api';
import {
  Card,
  CardHeader,
  CardContent,
  CardActions,
  Avatar,
  IconButton,
  Typography,
  Menu,
  MenuItem,
  Box,
  Link,
} from '@mui/material';
import {
  Favorite,
  FavoriteBorder,
  Comment,
  MoreVert,
  Delete,
} from '@mui/icons-material';
import Comments from './Comments';

const Post = ({ post, onPostDeleted, onPostLiked }) => {
  const { currentUser } = useContext(AuthContext);
  const [anchorEl, setAnchorEl] = useState(null);
  const [showComments, setShowComments] = useState(false);
  const [isLiking, setIsLiking] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLike = async () => {
    if (isLiking) return;
    
    try {
      setIsLiking(true);
      let response;
      
      if (post.likedByCurrentUser) {
        response = await unlikePost(post.id);
      } else {
        response = await likePost(post.id);
      }
      
      onPostLiked(response.data);
    } catch (err) {
      console.error('Failed to like/unlike post', err);
    } finally {
      setIsLiking(false);
    }
  };

  const handleDelete = async () => {
    if (isDeleting) return;
    
    try {
      setIsDeleting(true);
      await deletePost(post.id);
      onPostDeleted(post.id);
    } catch (err) {
      console.error('Failed to delete post', err);
    } finally {
      setIsDeleting(false);
      handleMenuClose();
    }
  };

  const toggleComments = () => {
    setShowComments(!showComments);
  };

  const isOwner = currentUser?.id === post.user.id;

  return (
    <Card sx={{ mb: 3 }}>
      <CardHeader
        avatar={
          <Avatar 
            src={post.user.profilePicture} 
            alt={post.user.username}
            component={RouterLink}
            to={`/profile/${post.user.id}`}
            sx={{ cursor: 'pointer' }}
          />
        }
        action={
          isOwner && (
            <>
              <IconButton aria-label="settings" onClick={handleMenuOpen}>
                <MoreVert />
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
              >
                <MenuItem onClick={handleDelete} disabled={isDeleting}>
                  <Delete fontSize="small" sx={{ mr: 1 }} />
                  {isDeleting ? 'Deleting...' : 'Delete'}
                </MenuItem>
              </Menu>
            </>
          )
        }
        title={
          <Link 
            component={RouterLink} 
            to={`/profile/${post.user.id}`}
            color="inherit"
            underline="hover"
          >
            {post.user.username}
          </Link>
        }
        subheader={new Date(post.createdAt).toLocaleString()}
      />
      <CardContent>
        <Typography variant="body1" component="p">
          {post.content}
        </Typography>
        
        {post.mediaUrls && post.mediaUrls.length > 0 && (
          <Box sx={{ mt: 2 }}>
            {post.mediaUrls.map((url, index) => (
              <img 
                key={index} 
                src={url || "/placeholder.svg"} 
                alt={`Post media ${index}`} 
                style={{ 
                  maxWidth: '100%', 
                  maxHeight: '400px',
                  borderRadius: '4px',
                  marginBottom: '8px'
                }} 
              />
            ))}
          </Box>
        )}
      </CardContent>
      <CardActions disableSpacing>
        <IconButton 
          aria-label="like" 
          onClick={handleLike}
          disabled={isLiking}
          color={post.likedByCurrentUser ? 'secondary' : 'default'}
        >
          {post.likedByCurrentUser ? <Favorite /> : <FavoriteBorder />}
        </IconButton>
        <Typography variant="body2" color="text.secondary">
          {post.likesCount} {post.likesCount === 1 ? 'like' : 'likes'}
        </Typography>
        
        <IconButton aria-label="comment" onClick={toggleComments} sx={{ ml: 2 }}>
          <Comment />
        </IconButton>
        <Typography variant="body2" color="text.secondary">
          {post.commentsCount} {post.commentsCount === 1 ? 'comment' : 'comments'}
        </Typography>
      </CardActions>
      
      {showComments && <Comments postId={post.id} />}
    </Card>
  );
};

export default Post;