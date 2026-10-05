package Project.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import Project.Helper.PageResponse;
import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Brand;
import Project.Model.Category;
import Project.Model.Product;
import Project.Model.DTO.BrandDTO;
import Project.Model.DTO.CategoryDTO;
import Project.Model.DTO.ProductRequestDTO;
import Project.Model.DTO.ProductResponseDTO;
import Project.Model.DTO.UserResponseDTO;
import Project.Repository.BrandRepository;
import Project.Repository.CategoryRepository;
import Project.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponseDTO convertProductToDTO(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setDescription(product.getDescription());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setQuantity(product.getQuantity());
        productResponseDTO.setImage(product.getImage());

        BrandDTO brandDTO = new BrandDTO(product.getBrand().getId(), 
            product.getBrand().getName(), product.getBrand().getDescription());
        productResponseDTO.setBrand(brandDTO);

        CategoryDTO categoryDTO = new CategoryDTO(product.getCategory().getId(),
            product.getCategory().getName(), product.getCategory().getDescription());
        productResponseDTO.setCategory(categoryDTO);
        return productResponseDTO;
    }

    public Product converDTOtoProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setQuantity(productRequestDTO.getQuantity());
        product.setImage(productRequestDTO.getImage());
        Brand brand = this.brandRepository.findById(productRequestDTO.getBrandId())
                            .orElseThrow(() -> new ResourceNotFoundException("Khong co brand"));

        product.setBrand(brand);

        Category category = this.categoryRepository.findById(productRequestDTO.getCategoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Khong co category"));
        
        product.setCategory(category);
        return product;
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = this.productRepository.save(converDTOtoProduct(productRequestDTO));
        return convertProductToDTO(product);
    }

    public Page<ProductResponseDTO> fetchProducts(Pageable pageable) { 
        return this.productRepository.findAll(pageable)
            .map(product -> {
                return convertProductToDTO(product);
            });
    }

    public ProductResponseDTO fetchProductById(int id) {
        Product product = this.productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("product not exist"));
        return convertProductToDTO(product);
    }

    public List<ProductResponseDTO> searchProducts(String keyword, String brand, Integer maxPrice, Integer minPrice, String type) {
        return this.productRepository.searchProducts(keyword, brand, maxPrice, minPrice, type)
                                    .stream()
                                    .map(product -> {
                                        return convertProductToDTO(product);
                                    }).collect(Collectors.toList());
    }

    public List<ProductResponseDTO> fetchProductByName(String name) {
        List<ProductResponseDTO> products = this.productRepository.findByName(name)
                                                    .stream()
                                                    .map(product -> {
                                                        return convertProductToDTO(product);
                                                    })
                                                    .collect(Collectors.toList());
        return products;
    }

    public Page<ProductResponseDTO> fetchProductWithBrandAndCategory(String brand, String category, Pageable pageable) {
        Page<ProductResponseDTO> productResponseDTOs = this.productRepository.findByBrand_nameAndCategory_name(brand, category, pageable)
                                                        .map(product -> {
                                                            return convertProductToDTO(product);
                                                        });
        return productResponseDTOs;
    }

    public Page<ProductResponseDTO> fetchProductWithBrand(String brand, Pageable pageable) {
        Page<ProductResponseDTO> productResponseDTOs = this.productRepository.findByBrand_name(brand, pageable)
                                                        .map(product -> {
                                                            return convertProductToDTO(product);
                                                        });
        return productResponseDTOs;
    }

    public Page<ProductResponseDTO> fetchProductWithCategory(String category, Pageable pageable) {
        Page<ProductResponseDTO> productResponseDTOs = this.productRepository.findByCategory_name(category, pageable)
                                                        .map(product -> {
                                                            return convertProductToDTO(product);
                                                        });
        return productResponseDTOs;
    }


    public ProductResponseDTO updateProductById(int id, ProductRequestDTO updateProduct) {
        Product productInDB = this.productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("product not exist"));

        productInDB.setName(updateProduct.getName());
        productInDB.setDescription(updateProduct.getDescription());
        productInDB.setPrice(updateProduct.getPrice());
        productInDB.setQuantity(updateProduct.getQuantity());
        Brand brand = this.brandRepository.findById(updateProduct.getBrandId())
                            .orElseThrow(() -> new ResourceNotFoundException("Khong co brand"));

        productInDB.setBrand(brand);

        Category category = this.categoryRepository.findById(updateProduct.getCategoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Khong co category"));
        
        productInDB.setCategory(category);
        this.productRepository.save(productInDB);
        return convertProductToDTO(productInDB);
    }

    public void deleteProductById(int id) {
        Product productInDB = this.productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("product not exist"));
        this.productRepository.delete(productInDB);
    }
}
