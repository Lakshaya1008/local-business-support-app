import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const ProfilePage = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        My Profile
      </Typography>
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="body1" color="text.secondary">
          Profile page coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default ProfilePage; 