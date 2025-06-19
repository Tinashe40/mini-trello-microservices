import { Box, Card, CardContent, Grid, Typography } from '@mui/material';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import { useAuthContext } from '../auth/AuthContext';

export function Dashboard() {
  const { user, logout } = useAuthContext();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate('/login');
    } else {
      axios.get('/auth/user')
        .then(response => {
          console.log('User data:', response.data);
        })
        .catch(error => {
          console.error('Error fetching user data:', error);
          logout();
          navigate('/login');
        });
    }
  }, [user, navigate, logout]);

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h4" gutterBottom>
        Welcome to Your Dashboard
      </Typography>
      <Typography variant="subtitle1" gutterBottom>
        Hello, <strong>{user?.username}</strong> 👋
      </Typography>

      <Grid container spacing={2} sx={{ mt: 2 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Projects</Typography>
              <Typography variant="body2">You have 3 active projects</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Tasks</Typography>
              <Typography variant="body2">5 tasks due today</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Messages</Typography>
              <Typography variant="body2">2 unread notifications</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
export default Dashboard;