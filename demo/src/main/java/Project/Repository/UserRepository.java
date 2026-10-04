package Project.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Project.Model.Product;
import Project.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { 
	Optional<User> findById(Integer id);
	
    Optional<User> findByName(String name);

	Optional<User> findByNameAndEmail(String name, String email);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findByRole_Name(String roleName);

}