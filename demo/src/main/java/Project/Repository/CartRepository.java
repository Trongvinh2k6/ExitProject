package Project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Cart;

@Repository 
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
}
