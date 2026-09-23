package Project.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceAlreadyExistsException;
import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Brand;
import Project.Model.DTO.BrandDTO;
import Project.Repository.BrandRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BrandService {
    private final BrandRepository brandRepository;

    public Brand createBrand(Brand inputBrand) {
        if (this.brandRepository.existsByName(inputBrand.getName())) { 
            throw new ResourceAlreadyExistsException( "Brand với name = " + inputBrand.getName() + " đã tồn tại" ); }
        return this.brandRepository.save(inputBrand);
    }

    public List<BrandDTO> fetchBrands() {
        List<BrandDTO> brandList = this.brandRepository.findAll().stream()
                        .map(brand -> BrandDTO.builder().id(brand.getId()).name(brand.getName()).description(brand.getDescription()).build())
                        .collect(Collectors.toList());
        return brandList;
    }

    public BrandDTO fetchBrandById(int id) { 
        Brand brandInDB = this.brandRepository.findById(id) 
        .orElseThrow(() -> new ResourceNotFoundException( "Không tồn tại brand với id = " + id )); 
        return BrandDTO.builder() .id(brandInDB.getId()) .name(brandInDB.getName()) .description(brandInDB.getDescription())
        .build(); 
    }

    public BrandDTO updateBrandById(int id, Brand updateBrand) {
        Brand brandInDB = this.brandRepository.findById(id) 
            .orElseThrow(() -> new ResourceNotFoundException( "Không tồn tại brand với id = " + id )); 

            if (brandInDB.getName().equals(updateBrand.getName())) { 
                brandInDB.setDescription(updateBrand.getDescription()); 
            } else { 
                // Tên thay đổi -> kiểm tra tên mới có bị trùng không 
                if (this.brandRepository.existsByNameAndIdNot( updateBrand.getName(), id)) { 
                    throw new ResourceAlreadyExistsException( "Brand với name = " + updateBrand.getName() + " đã tồn tại" ); 
                } 
                brandInDB.setName(updateBrand.getName()); 
                brandInDB.setDescription(updateBrand.getDescription()); 
            } 
            
            Brand savedBrand = this.brandRepository.save(brandInDB); 
            BrandDTO response = new BrandDTO(); 
            response.setId(savedBrand.getId()); 
            response.setName(savedBrand.getName()); 
            response.setDescription(savedBrand.getDescription());
            return response;
    }

    public void deleteBrandById(int id) { 
        if (!this.brandRepository.existsById(id)) { 
            throw new ResourceNotFoundException( "Không tồn tại brand với id = " + id ); 
        } 
        this.brandRepository.deleteById(id); 
    }
}
