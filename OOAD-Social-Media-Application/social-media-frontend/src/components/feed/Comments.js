// src/components/feed/Comments.js
import React, { useState, useEffect, useContext } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { getComments, createComment, deleteComment } from '../../api';
import {
  Box,
  Typography,
  TextField,
  Button,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  IconButton,
  Divider,
  CircularProgress,
  Link,
} from '@mui/material';
import { Delete, Send } from '@mui/icons-material';

const Comments = ({ postId }) => {
  const { currentUser } = useContext(AuthContext);
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const fetchComments = async () => {
    try {
      setLoading(true);
      const response = await getComments(postId);
      setComments(response.data.content);
    } catch (err) {
      setError('Failed to load comments');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) return;
    
    try {
      setSubmitting(true);
      const response = await createComment(postId, { content });
      setComments([response.data, ...comments]);
      setContent('');
    } catch (err) {
      setError('Failed to add comment');
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (commentId) => {
    try {
      await deleteComment(postId, commentId);
      setComments(comments.filter(comment => comment.id !== commentId));
    } catch (err) {
      setError('Failed to delete comment');
      console.error(err);
    }
  };

  return (
    <Box sx={{ p: 2, bgcolor: '#f5f5f5' }}>
      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', mb: 2 }}>
        <Avatar 
          src={currentUser?.profilePicture} 
          alt={currentUser?.username}
          sx={{ mr: 1 }}
        />
        <TextField
          fullWidth
          size="small"
          placeholder="Write a comment..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          variant="outlined"
          sx={{ mr: 1 }}
        />
        <Button
          type="submit"
          variant="contained"
          endIcon={<Send />}
          disabled={submitting || !content.trim()}
        >
          {submitting ? <CircularProgress size={24} /> : 'Post'}
        </Button>
      </Box>
      
      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}
      
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
          <CircularProgress />
        </Box>
      ) : comments.length === 0 ? (
        <Typography sx={{ textAlign: 'center', p: 2 }}>
          No comments yet. Be the first to comment!
        </Typography>
      ) : (
        <List>
          {comments.map((comment, index) => (
            <React.Fragment key={comment.id}>
              {index > 0 && <Divider variant="inset" component="li" />}
              <ListItem
                alignItems="flex-start"
                secondaryAction={
                  currentUser?.id === comment.user.id && (
                    <IconButton edge="end" aria-label="delete" onClick={() => handleDelete(comment.id)}>
                      <Delete fontSize="small" />
                    </IconButton>
                  )
                }
              >
                <ListItemAvatar>
                  <Avatar 
                    src={comment.user.profilePicture} 
                    alt={comment.user.username}
                    component={RouterLink}
                    to={`/profile/${comment.user.id}`}
                    sx={{ cursor: 'pointer' }}
                  />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Link 
                      component={RouterLink} 
                      to={`/profile/${comment.user.id}`}
                      color="inherit"
                      underline="hover"
                    >
                      {comment.user.username}
                    </Link>
                  }
                  secondary={
                    <>
                      <Typography
                        component="span"
                        variant="body2"
                        color="text.primary"
                        sx={{ display: 'block' }}
                      >
                        {comment.content}
                      </Typography>
                      <Typography
                        component="span"
                        variant="caption"
                        color="text.secondary"
                      >
                        {new Date(comment.createdAt).toLocaleString()}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
            </React.Fragment>
          ))}
        </List>
      )}
    </Box>
  );
};

export default Comments;