import React, { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Badge,
  Box,
  Menu,
  MenuItem,
  Avatar,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  useTheme,
  useMediaQuery,
  Container,
} from '@mui/material';
import {
  ShoppingCart as CartIcon,
  Person as PersonIcon,
  AdminPanelSettings as AdminIcon,
  Store as StoreIcon,
  Menu as MenuIcon,
  Home as HomeIcon,
  Inventory as ProductsIcon,
  Receipt as OrdersIcon,
  ExitToApp as LogoutIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useCart } from '../../contexts/CartContext';

const Navbar = () => {
  const { user, logout, isAuthenticated, isAdmin, isSeller } = useAuth();
  const { getCartItemCount } = useCart();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const [anchorEl, setAnchorEl] = useState(null);
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleProfileMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleLogout = () => {
    logout();
    handleMenuClose();
    navigate('/');
  };

  const menuItems = [
    { text: 'Home', icon: <HomeIcon />, path: '/' },
    { text: 'Products', icon: <ProductsIcon />, path: '/products' },
    ...(isAuthenticated ? [{ text: 'Orders', icon: <OrdersIcon />, path: '/orders' }] : []),
    ...(isAdmin ? [{ text: 'Admin', icon: <AdminIcon />, path: '/admin' }] : []),
    ...(isSeller ? [{ text: 'Seller', icon: <StoreIcon />, path: '/seller' }] : []),
  ];

  const drawer = (
    <Box>
      <Box sx={{ p: 2 }}>
        <Typography variant="h6" color="primary">
          VyapariVerse
        </Typography>
      </Box>
      <Divider />
      <List>
        {menuItems.map((item) => (
          <ListItem
            button
            key={item.text}
            component={RouterLink}
            to={item.path}
            onClick={handleDrawerToggle}
          >
            <ListItemIcon>{item.icon}</ListItemIcon>
            <ListItemText primary={item.text} />
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <>
      <AppBar position="static">
        <Container maxWidth="xl">
          <Toolbar disableGutters>
            <Typography
              variant="h6"
              noWrap
              component="a"
              href="/"
              sx={{
                mr: 2,
                display: { xs: 'none', md: 'flex' },
                fontFamily: 'monospace',
                fontWeight: 700,
                letterSpacing: '.3rem',
                color: 'inherit',
                textDecoration: 'none',
              }}
            >
              VyapariVerse
            </Typography>

            {isMobile && (
              <IconButton
                color="inherit"
                aria-label="open drawer"
                edge="start"
                onClick={handleDrawerToggle}
                sx={{ mr: 2 }}
              >
                <MenuIcon />
              </IconButton>
            )}

            <Typography
              variant="h5"
              noWrap
              component="a"
              href="/"
              sx={{
                mr: 2,
                display: { xs: 'flex', md: 'none' },
                flexGrow: 1,
                fontFamily: 'monospace',
                fontWeight: 700,
                letterSpacing: '.3rem',
                color: 'inherit',
                textDecoration: 'none',
              }}
            >
              VyapariVerse
            </Typography>

            {!isMobile && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Button
                  color="inherit"
                  component={RouterLink}
                  to="/products"
                  startIcon={<ProductsIcon />}
                >
                  Products
                </Button>

                {isAuthenticated && (
                  <Button
                    color="inherit"
                    component={RouterLink}
                    to="/orders"
                    startIcon={<OrdersIcon />}
                  >
                    Orders
                  </Button>
                )}

                {isAdmin && (
                  <Button
                    color="inherit"
                    component={RouterLink}
                    to="/admin"
                    startIcon={<AdminIcon />}
                  >
                    Admin
                  </Button>
                )}

                {isSeller && (
                  <Button
                    color="inherit"
                    component={RouterLink}
                    to="/seller"
                    startIcon={<StoreIcon />}
                  >
                    Seller
                  </Button>
                )}
              </Box>
            )}

            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <IconButton
                color="inherit"
                component={RouterLink}
                to="/cart"
                sx={{ ml: 1 }}
              >
                <Badge badgeContent={getCartItemCount()} color="secondary">
                  <CartIcon />
                </Badge>
              </IconButton>

              {isAuthenticated ? (
                <IconButton
                  color="inherit"
                  onClick={handleProfileMenuOpen}
                  sx={{ ml: 1 }}
                >
                  <Avatar sx={{ width: 32, height: 32 }}>
                    {user?.name?.charAt(0) || <PersonIcon />}
                  </Avatar>
                </IconButton>
              ) : (
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Button
                    color="inherit"
                    component={RouterLink}
                    to="/login"
                  >
                    Login
                  </Button>
                  <Button
                    variant="outlined"
                    color="inherit"
                    component={RouterLink}
                    to="/register"
                  >
                    Register
                  </Button>
                </Box>
              )}
            </Box>
          </Toolbar>
        </Container>
      </AppBar>

      {/* Mobile Drawer */}
      <Drawer
        variant="temporary"
        anchor="left"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{
          keepMounted: true,
        }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 240 },
        }}
      >
        {drawer}
      </Drawer>

      {/* Profile Menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <MenuItem
          component={RouterLink}
          to="/profile"
          onClick={handleMenuClose}
        >
          <ListItemIcon>
            <PersonIcon fontSize="small" />
          </ListItemIcon>
          Profile
        </MenuItem>
        <MenuItem onClick={handleLogout}>
          <ListItemIcon>
            <LogoutIcon fontSize="small" />
          </ListItemIcon>
          Logout
        </MenuItem>
      </Menu>
    </>
  );
};

export default Navbar; 