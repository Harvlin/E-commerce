package com.project.Ecommerce.service.interf;

import com.project.Ecommerce.domain.dto.CategoryDto;
import com.project.Ecommerce.domain.dto.response.Response;

public interface CategoryService {
    Response createCategory(CategoryDto categoryDto);
    Response updateCategory(Long categoryId, CategoryDto categoryDto);
    Response getAllCategories();
    Response getCategoryById(Long categoryId);
    Response deleteCategory(Long categoryId);
}
