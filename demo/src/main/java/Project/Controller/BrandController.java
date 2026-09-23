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
import Project.Model.Brand;
import Project.Model.DTO.BrandDTO;
import Project.Service.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping("/brands/create")
    public ResponseEntity<ApiResponse<Brand>> postBrand(
            @Valid @RequestBody Brand inputBrand) {

        Brand brand = this.brandService.createBrand(inputBrand);

        return ApiResponse.created(brand);
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getBrands() {

        List<BrandDTO> brands = this.brandService.fetchBrands();

        return ApiResponse.success(brands);
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrand(
            @PathVariable int id) {

        BrandDTO brand = this.brandService.fetchBrandById(id);

        return ApiResponse.success(brand);
    }

    @PutMapping("/brands/update/{id}")
    public ResponseEntity<ApiResponse<BrandDTO>> putBrand(
            @PathVariable int id,
            @Valid @RequestBody Brand inputBrand) {

        BrandDTO brand = this.brandService.updateBrandById(id, inputBrand);

        return ApiResponse.success(brand);
    }

    @DeleteMapping("/brands/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBrand(
            @PathVariable int id) {

        this.brandService.deleteBrandById(id);

        return ApiResponse.success("ok");
    }
}

