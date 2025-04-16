// src/components/feed/CreatePost.js
import React, { useState, useContext } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { createPost } from '../../api';
import {
  Card,
  CardContent,
  TextField,
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Avatar,
} from '@mui/material';
import { Image, Send } from '@mui/icons-material';

const CreatePost = ({ onPostCreated }) => {
  const { currentUser } = useContext(AuthContext);
  const [content, setContent] = useState('');
  const [privacyLevel, setPrivacyLevel] = useState('PUBLIC');
  const [mediaUrls, setMediaUrls] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setError('Post content cannot be empty');
      return;
    }
    
    try {
      setError('');
      setLoading(true);
      
      const response = await createPost({
        content,
        privacyLevel,
        mediaUrls,
      });
      
      setContent('');
      setMediaUrls([]);
      setPrivacyLevel('PUBLIC');
      onPostCreated(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };
  // social-media-frontend/src/components/feed/CreatePost.js
// Update the handleAddMedia function:

const handleAddMedia = (e) => {
  const file = e.target.files[0];
  if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setMediaUrls([...mediaUrls, reader.result]);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 2 }}>
          <Avatar 
            src={currentUser?.profilePicture} 
            alt={currentUser?.username}
            sx={{ mr: 2 }}
          />
          <Box sx={{ flexGrow: 1 }}>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}
            <form onSubmit={handleSubmit}>
              <TextField
                fullWidth
                multiline
                rows={3}
                placeholder={`What's on your mind, ${currentUser?.username}?`}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                variant="outlined"
                sx={{ mb: 2 }}
              />
              
              {mediaUrls.length > 0 && (
                <Box sx={{ mb: 2 }}>
                  {mediaUrls.map((url, index) => (
                    <img 
                      key={index} 
                      src={url || "/placeholder.svg"} 
                      alt={`Preview ${index}`} 
                      style={{ 
                        maxWidth: '100%', 
                        maxHeight: '200px',
                        borderRadius: '4px',
                        marginBottom: '8px'
                      }} 
                    />
                  ))}
                </Box>
              )}
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Box>
                  <input
                    accept="image/*"
                    id="icon-button-file"
                    type="file"
                    style={{ display: 'none' }}
                    onChange={handleAddMedia}
                  />
                  <label htmlFor="icon-button-file">
                    <Button
                      component="span"
                      startIcon={<Image />}
                      variant="outlined"
                      size="small"
                    >
                      Add Photo
                    </Button>
                  </label>
                  
                  <FormControl size="small" sx={{ ml: 2, minWidth: 120 }}>
                    <InputLabel id="privacy-label">Privacy</InputLabel>
                    <Select
                      labelId="privacy-label"
                      value={privacyLevel}
                      label="Privacy"
                      onChange={(e) => setPrivacyLevel(e.target.value)}
                    >
                      <MenuItem value="PUBLIC">Public</MenuItem>
                      <MenuItem value="FRIENDS">Friends</MenuItem>
                      <MenuItem value="PRIVATE">Private</MenuItem>
                    </Select>
                  </FormControl>
                </Box>
                
                <Button
                  type="submit"
                  variant="contained"
                  endIcon={<Send />}
                  disabled={loading}
                >
                  {loading ? 'Posting...' : 'Post'}
                </Button>
              </Box>
            </form>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default CreatePost;