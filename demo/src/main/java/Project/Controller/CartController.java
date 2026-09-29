package Project.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Model.DTO.CartItemRequestDTO;
import Project.Model.DTO.CartResponseDTO;
import Project.Service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/users/{userId}/cart/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addCartItem(
            @PathVariable int userId,
            @RequestBody CartItemRequestDTO cartItemRequestDTO) {

        CartResponseDTO cartResponseDTO =
                this.cartService.AddACartitemToCart(
                        cartItemRequestDTO,
                        userId
                );

        return ApiResponse.success(cartResponseDTO);
    }

    @PutMapping("/users/{userId}/cart/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItemQuantity(
            @PathVariable int userId,
            @PathVariable int productId,
            @RequestParam int newQuantity) {

        CartResponseDTO cartResponseDTO = 
                this.cartService.updateCartItemQuantity(userId, productId, newQuantity);

        return ApiResponse.success(cartResponseDTO);
    }

    @GetMapping("/users/{userId}/cart")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(
            @PathVariable int userId) {

        CartResponseDTO cartResponseDTO =
                this.cartService.fetchCartResponseDTO(userId);

        return ApiResponse.success(cartResponseDTO);
    }

    @DeleteMapping("/users/{userId}/delete/cart/items/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(
            @PathVariable int userId,
            @PathVariable int cartItemId) {

        this.cartService.deleteCartItemById(
                userId,
                cartItemId
        );

        return ApiResponse.success("delete cart item success");
    }
}
