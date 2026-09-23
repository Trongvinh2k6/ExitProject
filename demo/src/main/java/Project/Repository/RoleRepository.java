package Project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Integer id);

	Optional<Role> findByIdOrName(Integer id, String name);

	Optional<Role> findByName(String name);
}
