package com.project.Ecommerce.service.interf;

import com.project.Ecommerce.domain.dto.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface ProductService {
    Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price);

    Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price);

    Response deleteProduct(Long productId);

    Response getAllProducts();

    Response getProductById(Long productId);

    Response getProductByCategory(Long categoryId);

    Response searchProduct(String searchValue);
}