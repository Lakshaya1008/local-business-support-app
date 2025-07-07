import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const CheckoutPage = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Checkout
      </Typography>
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="body1" color="text.secondary">
          Checkout page coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default CheckoutPage; 