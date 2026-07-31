package com.courshare.course.config;

import com.courshare.course.domain.Category;
import com.courshare.course.domain.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> defaultCategories = List.of(
                "Development",
                "Business",
                "Design",
                "Marketing",
                "Cloud Computing"
        );

        for (String name : defaultCategories) {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(new Category(name));
            }
        }
    }
}
