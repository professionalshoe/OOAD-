// social-media-frontend/src/components/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import Navbar from './common/Navbar';
import Login from './auth/Login';
import Register from './auth/Register';
import Feed from './feed/Feed';
import Profile from './profile/Profile';
import PrivateRoute from './common/PrivateRoute';
import { Container, CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import './App.css'; // Import the CSS file

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <div className="app-container">
            <Navbar />
            <Container maxWidth="md" className="content-container">
              <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route 
                  path="/" 
                  element={
                    <PrivateRoute>
                      <Feed />
                    </PrivateRoute>
                  } 
                />
                <Route 
                  path="/profile/:userId" 
                  element={
                    <PrivateRoute>
                      <Profile />
                    </PrivateRoute>
                  } 
                />
                <Route path="*" element={<Navigate to="/" />} />
              </Routes>
            </Container>
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;