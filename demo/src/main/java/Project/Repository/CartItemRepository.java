package Project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Model.Cart;
import Project.Model.CartItem;

@Repository 
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
}
