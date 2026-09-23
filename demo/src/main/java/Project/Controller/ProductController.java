package Project.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Helper.PageResponse;
import Project.Model.Role;
import Project.Model.DTO.ProductRequestDTO;
import Project.Model.DTO.ProductResponseDTO;
import Project.Model.DTO.RoleResponseDTO;
import Project.Service.ProductService;
import Project.Service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products/create")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> postProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponseDTO = this.productService.createProduct(productRequestDTO);
        return ApiResponse.created(productResponseDTO);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            Pageable pageable) {

        Page<ProductResponseDTO> productResponseDTOs;

        if (brand != null && category != null) {
            productResponseDTOs =
                    this.productService.fetchProductWithBrandAndCategory(brand, category, pageable);

        } else if (brand != null) {
            productResponseDTOs =
                    this.productService.fetchProductWithBrand(brand, pageable);

        } else if (category != null) {
            productResponseDTOs =
                    this.productService.fetchProductWithCategory(category, pageable);

        } else {
            productResponseDTOs =
                    this.productService.fetchProducts(pageable);
        }

        return ApiResponse.success(PageResponse.from(productResponseDTOs));
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProduct(@PathVariable int id) {
        ProductResponseDTO productResponseDTO = this.productService.fetchProductById(id);
        return ApiResponse.success(productResponseDTO);
    }

    @PutMapping("/products/update/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable int id, 
        @Valid @RequestBody ProductRequestDTO updateProductRequestDTO) {
        ProductResponseDTO productResponseDTO = this.productService.updateProductById(id, updateProductRequestDTO);
        return ApiResponse.success(productResponseDTO);
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable int id) {
        this.productService.deleteProductById(id);
        return ApiResponse.success("delete successful");
    }
}
