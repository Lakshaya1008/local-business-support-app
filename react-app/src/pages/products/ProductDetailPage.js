import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const ProductDetailPage = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Product Details
      </Typography>
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="body1" color="text.secondary">
          Product detail page coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default ProductDetailPage; 