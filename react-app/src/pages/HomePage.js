import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Paper,
  useTheme,
} from '@mui/material';
import {
  Store as StoreIcon,
  ShoppingCart as CartIcon,
  Security as SecurityIcon,
  Support as SupportIcon,
  TrendingUp as TrendingUpIcon,
  LocalShipping as ShippingIcon,
} from '@mui/icons-material';

const HomePage = () => {
  const navigate = useNavigate();
  const theme = useTheme();

  const heroData = {
    title: 'VyapariVerse',
    description: 'Empowering Local Businesses - Connect with local businesses and discover unique products in your area.',
    buttonText: 'Get Started',
    buttonLink: '/products'
  };

  const features = [
    {
      icon: '🏪',
      title: 'Local Business Marketplace',
      description: 'Discover and shop from local businesses in your community.'
    },
    {
      icon: '🛒',
      title: 'Easy Shopping',
      description: 'Browse products, add to cart, and checkout seamlessly.'
    },
    {
      icon: '🚚',
      title: 'Fast Delivery',
      description: 'Quick and reliable delivery from local vendors.'
    },
    {
      icon: '💳',
      title: 'Secure Payments',
      description: 'Safe and secure payment processing for all transactions.'
    }
  ];

  const aboutData = {
    title: 'Why Choose VyapariVerse?',
    description: 'We provide comprehensive solutions for both customers and local businesses',
    points: [
      'Support local economy and community growth',
      'Discover unique products from neighborhood businesses',
      'Secure and reliable e-commerce platform',
      'Easy-to-use interface for both customers and vendors'
    ]
  };

  const ctaData = {
    title: 'Support Local Businesses',
    description: 'Discover and shop from local businesses in your community.',
    buttonText: 'Start Shopping',
    buttonLink: '/products'
  };

  return (
    <Box>
      {/* Hero Section */}
      <Paper
        sx={{
          position: 'relative',
          backgroundColor: 'grey.800',
          color: '#fff',
          mb: 4,
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundImage: 'url(https://images.unsplash.com/photo-1551434678-e076c223a692?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2070&q=80)',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            bottom: 0,
            right: 0,
            left: 0,
            backgroundColor: 'rgba(0,0,0,.3)',
          }}
        />
        <Grid container>
          <Grid item md={6}>
            <Box
              sx={{
                position: 'relative',
                p: { xs: 3, md: 6 },
                pr: { md: 0 },
              }}
            >
              <Typography component="h1" variant="h3" color="inherit" gutterBottom>
                {heroData.title}
              </Typography>
              <Typography variant="h5" color="inherit" paragraph>
                {heroData.description}
              </Typography>
              <Box sx={{ mt: 4 }}>
                <Button
                  variant="contained"
                  size="large"
                  onClick={() => navigate(heroData.buttonLink)}
                  sx={{ mr: 2, mb: 2 }}
                >
                  {heroData.buttonText}
                </Button>
                <Button
                  variant="outlined"
                  size="large"
                  onClick={() => navigate('/register')}
                  sx={{ color: 'white', borderColor: 'white' }}
                >
                  Join as Seller
                </Button>
              </Box>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h4" align="center" gutterBottom>
          {aboutData.title}
        </Typography>
        <Typography variant="subtitle1" align="center" color="text.secondary" paragraph>
          {aboutData.description}
        </Typography>
        
        <Grid container spacing={4} sx={{ mt: 4 }}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card
                sx={{
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    transition: 'transform 0.3s ease-in-out',
                    boxShadow: theme.shadows[8],
                  },
                }}
              >
                <CardContent sx={{ flexGrow: 1, textAlign: 'center' }}>
                  <Box sx={{ color: 'primary.main', mb: 2 }}>
                    {feature.icon}
                  </Box>
                  <Typography gutterBottom variant="h6" component="h2">
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* CTA Section */}
      <Box sx={{ bgcolor: 'primary.main', color: 'white', py: 8 }}>
        <Container maxWidth="md">
          <Typography variant="h4" align="center" gutterBottom>
            {ctaData.title}
          </Typography>
          <Typography variant="subtitle1" align="center" paragraph>
            {ctaData.description}
          </Typography>
          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 4 }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/register')}
              sx={{ bgcolor: 'white', color: 'primary.main', '&:hover': { bgcolor: 'grey.100' } }}
            >
              Sign Up Now
            </Button>
            <Button
              variant="outlined"
              size="large"
              onClick={() => navigate('/products')}
              sx={{ color: 'white', borderColor: 'white' }}
            >
              Explore Products
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default HomePage; 