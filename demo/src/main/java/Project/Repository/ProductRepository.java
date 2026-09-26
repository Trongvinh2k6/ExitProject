package Project.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Product;

@Repository 
public interface ProductRepository extends JpaRepository<Product, Integer>{
    Page<Product> findByBrand_name(String brand, Pageable pageable);

    Page<Product> findByCategory_name(String category, Pageable pageable);

    Page<Product> findByBrand_nameAndCategory_name(String brand, String name, Pageable pageable);

    List<Product> findByName(String name);
}
