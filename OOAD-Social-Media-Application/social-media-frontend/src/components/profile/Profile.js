// src/components/profile/Profile.js
// social-media-frontend/src/components/profile/Profile.js
import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { getUserProfile, followUser, unfollowUser } from '../../api';
import Post from '../feed/Post';
import {
  Box,
  Typography,
  Avatar,
  Button,
  Tabs,
  Tab,
  Divider,
  Paper,
  CircularProgress,
} from '@mui/material';
import './Profile.css'; // Import the CSS file

const Profile = () => {
  const { userId } = useParams();
  const { currentUser } = useContext(AuthContext);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tabValue, setTabValue] = useState(0);
  const [following, setFollowing] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, [userId]);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await getUserProfile(userId);
      setProfile(response.data);
      // In a real app, you would check if the current user is following this profile
      setFollowing(false);
    } catch (err) {
      setError('Failed to load profile');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleFollowToggle = async () => {
    if (actionLoading) return;
    
    try {
      setActionLoading(true);
      
      if (following) {
        await unfollowUser(userId);
      } else {
        await followUser(userId);
      }
      
      setFollowing(!following);
      // Refresh profile to get updated counts
      fetchProfile();
    } catch (err) {
      console.error('Failed to follow/unfollow user', err);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Typography color="error" sx={{ mt: 4, textAlign: 'center' }}>
        {error}
      </Typography>
    );
  }

  if (!profile) {
    return (
      <Typography sx={{ mt: 4, textAlign: 'center' }}>
        User not found
      </Typography>
    );
  }

  const isCurrentUser = currentUser?.id === parseInt(userId);

  return (
    <Box>
      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <Avatar
            src={profile.profilePicture}
            alt={profile.username}
            sx={{ width: 100, height: 100, mr: 3 }}
          />
          <Box>
            <Typography variant="h5" component="h1">
              {profile.username}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
              {profile.email}
            </Typography>
            <Box sx={{ display: 'flex', gap: 2 }}>
              <Typography variant="body2">
                <strong>{profile.followersCount}</strong> followers
              </Typography>
              <Typography variant="body2">
                <strong>{profile.followingCount}</strong> following
              </Typography>
            </Box>
          </Box>
          
          {!isCurrentUser && (
            <Button
              variant={following ? 'outlined' : 'contained'}
              sx={{ ml: 'auto' }}
              onClick={handleFollowToggle}
              disabled={actionLoading}
            >
              {following ? 'Unfollow' : 'Follow'}
            </Button>
          )}
        </Box>
        
        {profile.bio && (
          <Typography variant="body1" sx={{ mt: 2 }}>
            {profile.bio}
          </Typography>
        )}
      </Paper>
      
      <Box sx={{ width: '100%' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange} centered>
            <Tab label="Posts" />
            <Tab label="Photos" />
            <Tab label="Likes" />
          </Tabs>
        </Box>
        <Box sx={{ p: 3 }}>
          {tabValue === 0 && (
            <Box>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Posts
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {/* In a real app, you would fetch and display the user's posts here */}
              <Typography sx={{ textAlign: 'center', py: 4 }}>
                No posts yet
              </Typography>
            </Box>
          )}
          {tabValue === 1 && (
            <Box>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Photos
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {/* In a real app, you would fetch and display the user's photos here */}
              <Typography sx={{ textAlign: 'center', py: 4 }}>
                No photos yet
              </Typography>
            </Box>
          )}
          {tabValue === 2 && (
            <Box>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Likes
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {/* In a real app, you would fetch and display the user's liked posts here */}
              <Typography sx={{ textAlign: 'center', py: 4 }}>
                No liked posts yet
              </Typography>
            </Box>
          )}
        </Box>
      </Box>
    </Box>
  );
};

export default Profile;