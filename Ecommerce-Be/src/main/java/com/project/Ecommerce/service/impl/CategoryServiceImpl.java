package com.project.Ecommerce.service.impl;

import com.project.Ecommerce.domain.dto.CategoryDto;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.CategoryEntity;
import com.project.Ecommerce.exception.NotFoundException;
import com.project.Ecommerce.mapper.EntityDtoMapper;
import com.project.Ecommerce.repository.CategoryRepository;
import com.project.Ecommerce.service.interf.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityDtoMapper entityDtoMapper;

    @Override
    public Response createCategory(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryDto.getName());
        categoryRepository.save(categoryEntity);
        return Response.builder()
                .status(200)
                .message("Category created")
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        categoryEntity.setName(categoryDto.getName());
        categoryRepository.save(categoryEntity);
        return Response.builder()
                .status(200)
                .message("Category updated")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = categoryEntities.stream()
                .map(entityDtoMapper::mapCategoryToDtoBasic)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .categoryList(categoryDtos)
                .build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        CategoryDto categoryDto = entityDtoMapper.mapCategoryToDtoBasic(categoryEntity);
        return Response.builder()
                .status(200)
                .category(categoryDto)
                .build();
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(categoryEntity);
        return Response.builder()
                .status(200)
                .message("Category deleted")
                .build();
    }
}
