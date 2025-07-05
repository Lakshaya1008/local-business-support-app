package com.example.localbusiness.service;

import com.example.localbusiness.dto.ProductRequest;
import com.example.localbusiness.dto.ProductResponse;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.model.Role;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private User testSeller;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(100.00));
        productRequest.setQuantity(10);
        productRequest.setCategory("Test Category");

        testSeller = User.builder()
                .id(1L)
                .email("seller@example.com")
                .name("Test Seller")
                .roles(Set.of(Role.SELLER))
                .enabled(true)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100.00))
                .quantity(10)
                .category("Test Category")
                .seller(testSeller)
                .build();
    }

    @Test
    void addProduct_ShouldCreateNewProduct_WhenValidRequest() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("seller@example.com");
        when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(testSeller));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        SecurityContextHolder.setContext(securityContext);

        // When
        ProductResponse response = productService.addProduct(productRequest);

        // Then
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(BigDecimal.valueOf(100.00), response.getPrice());
        assertEquals(10, response.getQuantity());
        assertEquals("Test Category", response.getCategory());
        assertEquals("seller@example.com", response.getSellerEmail());

        verify(userRepository).findByEmail("seller@example.com");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenValidRequest() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("seller@example.com");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        SecurityContextHolder.setContext(securityContext);

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(BigDecimal.valueOf(150.00));
        updateRequest.setQuantity(20);
        updateRequest.setCategory("Updated Category");

        // When
        ProductResponse response = productService.updateProduct(1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("Updated Product", response.getName());
        assertEquals("Updated Description", response.getDescription());
        assertEquals(BigDecimal.valueOf(150.00), response.getPrice());
        assertEquals(20, response.getQuantity());
        assertEquals("Updated Category", response.getCategory());

        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other@example.com");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        SecurityContextHolder.setContext(securityContext);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.updateProduct(1L, productRequest));
        assertEquals("Unauthorized", exception.getMessage());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct_WhenAuthorized() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("seller@example.com");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        SecurityContextHolder.setContext(securityContext);

        // When
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other@example.com");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        SecurityContextHolder.setContext(securityContext);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.deleteProduct(1L));
        assertEquals("Unauthorized", exception.getMessage());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getSellerProducts_ShouldReturnSellerProducts() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("seller@example.com");
        when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(testSeller));
        when(productRepository.findBySeller(testSeller)).thenReturn(Arrays.asList(testProduct));

        SecurityContextHolder.setContext(securityContext);

        // When
        List<ProductResponse> responses = productService.getSellerProducts();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Product", responses.get(0).getName());

        verify(userRepository).findByEmail("seller@example.com");
        verify(productRepository).findBySeller(testSeller);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Description 2")
                .price(BigDecimal.valueOf(200.00))
                .quantity(5)
                .category("Category 2")
                .seller(testSeller)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        // When
        List<ProductResponse> responses = productService.getAllProducts();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Product", responses.get(0).getName());
        assertEquals("Product 2", responses.get(1).getName());

        verify(productRepository).findAll();
    }

    @Test
    void searchProducts_ShouldReturnFilteredProducts_WhenNameProvided() {
        // Given
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testProduct));

        // When
        List<ProductResponse> responses = productService.searchProducts("Test", null);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Product", responses.get(0).getName());

        verify(productRepository).findByNameContainingIgnoreCase("Test");
        verify(productRepository, never()).findByCategoryContainingIgnoreCase(anyString());
    }

    @Test
    void searchProducts_ShouldReturnFilteredProducts_WhenCategoryProvided() {
        // Given
        when(productRepository.findByCategoryContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testProduct));

        // When
        List<ProductResponse> responses = productService.searchProducts(null, "Test");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Product", responses.get(0).getName());

        verify(productRepository).findByCategoryContainingIgnoreCase("Test");
        verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void searchProducts_ShouldReturnAllProducts_WhenNoFiltersProvided() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        // When
        List<ProductResponse> responses = productService.searchProducts(null, null);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(productRepository).findAll();
        verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
        verify(productRepository, never()).findByCategoryContainingIgnoreCase(anyString());
    }
} 