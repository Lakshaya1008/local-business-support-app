package com.example.localbusiness.service;

import com.example.localbusiness.dto.*;
import com.example.localbusiness.model.*;
import com.example.localbusiness.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductResponse addProduct(ProductRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User seller = userRepository.findByEmail(email).orElseThrow();
            
            Product product = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .category(request.getCategory())
                    .seller(seller)
                    .build();
            productRepository.save(product);
            
            log.info("Product created successfully - ID: {}, Name: {}, Seller: {}", 
                product.getId(), product.getName(), seller.getEmail());
            return toResponse(product);
        } catch (Exception e) {
            log.error("Error creating product: {}", request.getName(), e);
            throw e;
        }
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        try {
            Product product = productRepository.findById(id).orElseThrow();
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!product.getSeller().getEmail().equals(email)) {
                log.warn("Unauthorized product update attempt - Product ID: {}, User: {}", id, email);
                throw new RuntimeException("Unauthorized");
            }
            
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setCategory(request.getCategory());
            productRepository.save(product);
            
            log.info("Product updated successfully - ID: {}, Name: {}", id, product.getName());
            return toResponse(product);
        } catch (Exception e) {
            log.error("Error updating product ID: {}", id, e);
            throw e;
        }
    }

    public void deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id).orElseThrow();
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!product.getSeller().getEmail().equals(email)) {
                log.warn("Unauthorized product deletion attempt - Product ID: {}, User: {}", id, email);
                throw new RuntimeException("Unauthorized");
            }
            productRepository.delete(product);
            log.info("Product deleted successfully - ID: {}, Name: {}", id, product.getName());
        } catch (Exception e) {
            log.error("Error deleting product ID: {}", id, e);
            throw e;
        }
    }

    public List<ProductResponse> getSellerProducts() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User seller = userRepository.findByEmail(email).orElseThrow();
            List<ProductResponse> products = productRepository.findBySeller(seller)
                .stream().map(this::toResponse).collect(Collectors.toList());
            
            log.info("Retrieved {} products for seller: {}", products.size(), email);
            return products;
        } catch (Exception e) {
            log.error("Error retrieving seller products", e);
            throw e;
        }
    }

    public List<ProductResponse> getAllProducts() {
        try {
            List<ProductResponse> products = productRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
            
            log.info("Retrieved {} products", products.size());
            return products;
        } catch (Exception e) {
            log.error("Error retrieving all products", e);
            throw e;
        }
    }

    public List<ProductResponse> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            List<Product> products;
            if (name != null && !name.isEmpty()) {
                products = productRepository.findByNameContainingIgnoreCase(name);
                log.info("Searching products by name: '{}', found: {}", name, products.size());
            } else if (category != null && !category.isEmpty()) {
                products = productRepository.findByCategoryContainingIgnoreCase(category);
                log.info("Searching products by category: '{}', found: {}", category, products.size());
            } else {
                products = productRepository.findAll();
                log.info("No search criteria provided, returning all products: {}", products.size());
            }
            
            // Apply price range filter if provided
            if (minPrice != null || maxPrice != null) {
                products = products.stream()
                    .filter(product -> {
                        if (minPrice != null && product.getPrice().compareTo(minPrice) < 0) {
                            return false;
                        }
                        if (maxPrice != null && product.getPrice().compareTo(maxPrice) > 0) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
                log.info("Applied price range filter (min: {}, max: {}), filtered to: {}", minPrice, maxPrice, products.size());
            }
            
            return products.stream().map(this::toResponse).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching products - name: {}, category: {}, minPrice: {}, maxPrice: {}", 
                name, category, minPrice, maxPrice, e);
            throw e;
        }
    }

    public ProductResponse getProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            log.info("Retrieved product - ID: {}, Name: {}", id, product.getName());
            return toResponse(product);
        } catch (Exception e) {
            log.error("Error retrieving product ID: {}", id, e);
            throw e;
        }
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse resp = new ProductResponse();
        resp.setId(product.getId());
        resp.setName(product.getName());
        resp.setDescription(product.getDescription());
        resp.setPrice(product.getPrice());
        resp.setQuantity(product.getQuantity());
        resp.setCategory(product.getCategory());
        resp.setSellerEmail(product.getSeller().getEmail());
        return resp;
    }
} 