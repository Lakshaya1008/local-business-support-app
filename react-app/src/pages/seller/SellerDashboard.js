import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const SellerDashboard = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Seller Dashboard
      </Typography>
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="body1" color="text.secondary">
          Seller dashboard coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default SellerDashboard; 