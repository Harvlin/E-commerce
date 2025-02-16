package com.project.Ecommerce.service.impl;

import com.project.Ecommerce.domain.dto.ProductDto;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.CategoryEntity;
import com.project.Ecommerce.domain.entity.ProductEntity;
import com.project.Ecommerce.exception.NotFoundException;
import com.project.Ecommerce.mapper.EntityDtoMapper;
import com.project.Ecommerce.repository.CategoryRepository;
import com.project.Ecommerce.repository.ProductRepository;
import com.project.Ecommerce.service.AwsS3Service;
import com.project.Ecommerce.service.interf.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final AwsS3Service awsS3Service;

    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        String productImageUrl = awsS3Service.saveImageToS3(image);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setCategory(categoryEntity);
        productEntity.setImageUrl(productImageUrl);
        productEntity.setName(name);
        productEntity.setDescription(description);
        productEntity.setPrice(price);

        productRepository.save(productEntity);
        return Response.builder()
                .status(200)
                .message("Product created")
                .build();
    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));

        CategoryEntity categoryEntity = null;
        String productImageUrl = null;

        if (categoryId != null) {
            categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        }

        if (image != null) {
            productImageUrl = awsS3Service.saveImageToS3(image);
        }

        if (categoryEntity != null) productEntity.setCategory(categoryEntity);
        if (name != null) productEntity.setName(name);
        if (description != null) productEntity.setDescription(description);
        if (price != null) productEntity.setPrice(price);
        if (productImageUrl != null) productEntity.setImageUrl(productImageUrl);

        productRepository.save(productEntity);
        return Response.builder()
                .status(200)
                .message("Product updated")
                .build();
    }

    @Override
    public Response deleteProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.delete(productEntity);

        return Response.builder()
                .status(200)
                .message("Product deleted")
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<ProductDto> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(products)
                .build();
    }

    @Override
    public Response getProductById(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(productEntity);

        return Response.builder()
                .status(200)
                .message("Product updated")
                .build();
    }

    @Override
    public Response getProductByCategory(Long categoryId) {
        List<ProductEntity> productEntities = productRepository.findByCategoryId(categoryId);
        if (productEntities.isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        List<ProductDto> productDtoList = productEntities.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }

    @Override
    public Response searchProduct(String searchValue) {
        List<ProductEntity> productEntities = productRepository.findByNameContainingOrDescriptionContaining(searchValue, searchValue);
        if (productEntities.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        List<ProductDto> productDtoList = productEntities.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }
}
