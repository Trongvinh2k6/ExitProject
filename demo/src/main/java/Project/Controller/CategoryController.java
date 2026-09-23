package Project.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Model.Category;
import Project.Model.DTO.CategoryDTO;
import Project.Service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories/create")
    public ResponseEntity<ApiResponse<Category>> postCategory(
            @Valid @RequestBody Category inputCategory) {

        Category category = this.categoryService.createCategory(inputCategory);

        return ApiResponse.created(category);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {

        List<CategoryDTO> categories = this.categoryService.fetchCategories();

        return ApiResponse.success(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategory(
            @PathVariable int id) {

        CategoryDTO category = this.categoryService.fetchCategoryById(id);

        return ApiResponse.success(category);
    }

    @PutMapping("/categories/update/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> putCategory(
            @PathVariable int id,
            @Valid @RequestBody Category inputCategory) {

        CategoryDTO category =
                this.categoryService.updateCategoryById(id, inputCategory);

        return ApiResponse.success(category);
    }

    @DeleteMapping("/categories/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable int id) {

        this.categoryService.deleteCategoryById(id);

        return ApiResponse.success("ok");
    }
}

