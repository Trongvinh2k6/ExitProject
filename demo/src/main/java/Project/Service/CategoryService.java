package Project.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceAlreadyExistsException;
import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Category;
import Project.Model.DTO.CategoryDTO;
import Project.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // CREATE
    public Category createCategory(Category inputCategory) {

        if (this.categoryRepository.existsByName(inputCategory.getName())) {
            throw new ResourceAlreadyExistsException(
                "Category với name = " + inputCategory.getName() + " đã tồn tại"
            );
        }

        return this.categoryRepository.save(inputCategory);
    }

    // GET ALL
    public List<CategoryDTO> fetchCategories() {

        List<CategoryDTO> categoryList = this.categoryRepository.findAll()
            .stream()
            .map(category -> CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build()
            )
            .collect(Collectors.toList());

        return categoryList;
    }

    // GET BY ID
    public CategoryDTO fetchCategoryById(int id) {

        Category categoryInDB = this.categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Không tồn tại category với id = " + id
            ));

        return CategoryDTO.builder()
            .id(categoryInDB.getId())
            .name(categoryInDB.getName())
            .description(categoryInDB.getDescription())
            .build();
    }

    // UPDATE
    public CategoryDTO updateCategoryById(int id, Category updateCategory) {

        Category categoryInDB = this.categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Không tồn tại category với id = " + id
            ));

        // Tên không thay đổi
        if (categoryInDB.getName().equals(updateCategory.getName())) {

            categoryInDB.setDescription(updateCategory.getDescription());

        } else {

            // Tên thay đổi -> kiểm tra tên mới có bị trùng không
            if (this.categoryRepository.existsByNameAndIdNot(
                    updateCategory.getName(), id)) {

                throw new ResourceAlreadyExistsException(
                    "Category với name = " + updateCategory.getName()
                    + " đã tồn tại"
                );
            }

            categoryInDB.setName(updateCategory.getName());
            categoryInDB.setDescription(updateCategory.getDescription());
        }

        Category savedCategory = this.categoryRepository.save(categoryInDB);

        CategoryDTO response = new CategoryDTO();
        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());
        response.setDescription(savedCategory.getDescription());

        return response;
    }

    // DELETE
    public void deleteCategoryById(int id) {

        if (!this.categoryRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                "Không tồn tại category với id = " + id
            );
        }

        this.categoryRepository.deleteById(id);
    }

    public List<String> getCategoryNames() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .toList();
    }
}

