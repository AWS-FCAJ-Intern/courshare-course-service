package com.courshare.course.api;

import com.courshare.course.api.dto.CategoryRequest;
import com.courshare.course.api.dto.CategoryResponse;
import com.courshare.course.application.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnList_WhenPublic() throws Exception {
        Mockito.when(categoryService.getAllCategories())
                .thenReturn(List.of(new CategoryResponse("1", "Development")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Development"));
    }

    @Test
    void createCategory_ShouldReturnCreated() throws Exception {
        CategoryRequest request = new CategoryRequest("Development");
        CategoryResponse response = new CategoryResponse("1", "Development");

        Mockito.when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Development"));
    }

    @Test
    void updateCategory_ShouldReturnOk() throws Exception {
        CategoryRequest request = new CategoryRequest("Design");
        CategoryResponse response = new CategoryResponse("1", "Design");

        Mockito.when(categoryService.updateCategory(eq("1"), any(CategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Design"));
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory("1");

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }
}
