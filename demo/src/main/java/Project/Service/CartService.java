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

    public CartResponseDTO AddACartitemToCart(
        CartItemRequestDTO cartItemRequestDTO,
        int userId
    ) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong tim thay user")
                );

        Product product = this.productRepository
                .findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong tim thay product")
                );

        int quantity = cartItemRequestDTO.getQuantity();

        // Kiểm tra số lượng
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai lon hon 0"
            );
        }

        // Kiểm tra tồn kho
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Khong du so luong san pham trong kho"
            );
        }

        Cart cart = user.getCart();

        boolean ok = false;

        for (CartItem cartItem : cart.getItems()) {

            if (cartItem.getProduct().getId()
                    .equals(product.getId())) {

                // Sản phẩm đã có trong giỏ
                int newQuantity =
                        cartItem.getQuantity() + quantity;

                cartItem.setQuantity(newQuantity);

                cartItem.setPrice(
                        product.getPrice() * newQuantity
                );

                this.cartItemRepository.save(cartItem);

                ok = true;
                break;
            }
        }

        // Sản phẩm chưa có trong giỏ
        if (!ok) {

            CartItem cartItem = new CartItem();

            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice() * quantity);
            cartItem.setCart(cart);

            this.cartItemRepository.save(cartItem);

            cart.getItems().add(cartItem);
        }

        // Trừ số lượng trong kho
        product.setQuantity(
                product.getQuantity() - quantity
        );

        this.productRepository.save(product);

        // Lưu Cart
        this.cartRepository.save(cart);

        return convertCartToDTO(cart);
    }

    public CartResponseDTO updateCartItemQuantity(int userId, int productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException("User không có giỏ hàng");
        }

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product"));

        if (product.getQuantity() < newQuantity) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ");
        }

        boolean updated = false;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                cartItem.setQuantity(newQuantity);
                cartItem.setPrice(product.getPrice() * newQuantity);
                this.cartItemRepository.save(cartItem);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        this.cartRepository.save(cart);
        return convertCartToDTO(cart);
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

        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                                                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don hang"));
        
        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());
        this.productRepository.save(product);

        boolean removed = cart.getItems()
                .removeIf(item -> item.getId().equals(cartItemId));

        this.cartRepository.save(cart);
        if (!removed) {
            throw new ResourceNotFoundException("Khong tim thay cart item");
        }
    }
}
