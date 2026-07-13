package com.courshare.course.application;

import com.courshare.course.api.dto.CategoryRequest;
import com.courshare.course.api.dto.CategoryResponse;
import com.courshare.course.domain.Category;
import com.courshare.course.domain.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Category already exists with name: " + request.name());
        }
        Category category = new Category(request.name());
        category = categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Transactional
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Category already exists with name: " + request.name());
        }

        category.setName(request.name());
        category = categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Transactional
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
