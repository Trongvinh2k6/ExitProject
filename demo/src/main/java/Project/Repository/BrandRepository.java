package Project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Brand;

@Repository 
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);
}
