package Project.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Cart;
import Project.Model.CartItem;
import Project.Model.Product;
import Project.Model.User;
import Project.Model.DTO.BrandDTO;
import Project.Model.DTO.CartItemRequestDTO;
import Project.Model.DTO.CartItemResponseDTO;
import Project.Model.DTO.CartResponseDTO;
import Project.Model.DTO.CategoryDTO;
import Project.Model.DTO.ProductResponseDTO;
import Project.Repository.CartItemRepository;
import Project.Repository.CartRepository;
import Project.Repository.ProductRepository;
import Project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductResponseDTO convertProductToDTO(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setDescription(product.getDescription());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setQuantity(product.getQuantity());

        BrandDTO brandDTO = new BrandDTO(product.getBrand().getId(), 
            product.getBrand().getName(), product.getBrand().getDescription());
        productResponseDTO.setBrand(brandDTO);

        CategoryDTO categoryDTO = new CategoryDTO(product.getCategory().getId(),
            product.getCategory().getName(), product.getCategory().getDescription());
        productResponseDTO.setCategory(categoryDTO);
        return productResponseDTO;
    }

    public CartItemResponseDTO convertCartItemToDTO(CartItem cartItem) {
        return CartItemResponseDTO.builder().id(cartItem.getId())
                                            .productResponseDTO(this.convertProductToDTO(cartItem.getProduct()))
                                            .quantity(cartItem.getQuantity())
                                            .price(cartItem.getPrice()).build();

    }

    public CartResponseDTO convertCartToDTO(Cart cart) {
        List<CartItemResponseDTO> CartResponseDTOs = cart.getItems().stream()
                                                            .map(cartItem -> {return convertCartItemToDTO(cartItem);})
                                                            .collect(Collectors.toList());
        return CartResponseDTO.builder()
                                .id(cart.getId())
                                .items(CartResponseDTOs)
                                .build();
    }

    public CartResponseDTO AddACartitemToCart(CartItemRequestDTO cartItemRequestDTO, int userId) {
        User user = this.userRepository.findById(userId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user"));
        
        Product product = this.productRepository.findById(cartItemRequestDTO.getProduct_Id())
                                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay product"));

        if (product.getQuantity() <= 0) {
            throw new ResourceNotFoundException("San pham da het hang");
        }
        product.setQuantity(product.getQuantity() - 1);
        Cart cart = user.getCart();
        boolean ok = false;
        for(CartItem cartItem : cart.getItems()) {
            if(cartItem.getProduct().getId().equals(product.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItem.setPrice(product.getPrice() * cartItem.getQuantity());
                ok = true;
                break;
            }
        }
        if(ok == false) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setPrice(cartItemRequestDTO.getPrice());
            cartItem.setCart(cart);

            this.cartItemRepository.save(cartItem);

            cart.getItems().add(cartItem);
        }

        this.productRepository.save(product);
        return convertCartToDTO(this.cartRepository.save(cart));
    }

    public CartResponseDTO fetchCartResponseDTO(int userId) {
        User user = this.userRepository.findById(userId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user"));
        
        Cart cart = user.getCart();

        if (cart == null) {
            throw new ResourceNotFoundException("User khong co cart");
        }

        return convertCartToDTO(cart);
    }

    public void deleteCartItemById(int userId, int cartItemId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user"));

        Cart cart = user.getCart();

        if (cart == null) {
            throw new ResourceNotFoundException("User khong co cart");
        }

        boolean removed = cart.getItems()
                .removeIf(item -> item.getId().equals(cartItemId));

        this.cartRepository.save(cart);
        if (!removed) {
            throw new ResourceNotFoundException("Khong tim thay cart item");
        }
    }
}
