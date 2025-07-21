import { Box, Button, Typography } from "@mui/material";
import { useContext, useEffect } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router-dom";
import { setCredentials } from "./store/authSlice";

import ActivityDetail from "./components/ActivityDetail";
import Dashboard from "./components/Dashboard";

function App() {
  const { token, tokenData, logIn, logOut, error } = useContext(AuthContext);
  const dispatch = useDispatch();

  useEffect(() => {
    if (token) dispatch(setCredentials({ token, user: tokenData }));
  }, [token, tokenData, dispatch]);

  if (error) {
    return (
      <Box p={4}>
        <Typography color="error">{error}</Typography>
        <Button onClick={() => logIn({ prompt: 'login' })}>Try again</Button>
      </Box>
    );
  }

  return (
    <Router>
      {!token ? (
        <Box
          sx={{
            height: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            textAlign: 'center',
          }}
        >
          <Typography variant="h4" gutterBottom>
            Welcome to the Fitness Tracker App
          </Typography>
          <Typography variant="subtitle1" sx={{ mb: 3 }}>
            Please login to access your activities
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={() => {
              ['ROCP_idToken', 'ROCP_refreshToken', 'ROCP_refreshTokenExpire', 'token', 'user', 'userId']
                .forEach((key) => localStorage.removeItem(key));
              logIn({ prompt: 'login' });
            }}
          >
            LOGIN
          </Button>
        </Box>
      ) : (
        <Box sx={{ px: 2, pt: 6 }}>
          {/* Logout button in top-right corner */}
          <Box
            sx={{
              position: 'absolute',
              top: 16,
              right: 24,
              zIndex: 1000,
            }}
          >
            <Button variant="contained" onClick={() => logOut()}>
              Logout
            </Button>
          </Box>

          <Routes>
            <Route path="/" element={<Navigate to="/activities" replace />} />
            <Route path="/activities" element={<Dashboard />} />
            <Route path="/activities/:id" element={<ActivityDetail />} />
            <Route path="*" element={<Navigate to="/activities" replace />} />
          </Routes>
        </Box>
      )}
    </Router>
  );
}

export default App;
