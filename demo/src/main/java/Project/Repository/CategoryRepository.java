package Project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);
}