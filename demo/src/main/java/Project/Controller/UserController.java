package Project.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Helper.PageResponse;
import Project.Model.DTO.UserRequestDTO;
import Project.Model.DTO.UserResponseDTO;
import Project.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users/create")
	public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO inputUser) {
		UserResponseDTO userInDB = this.userService.createUser(inputUser);
		return ApiResponse.created(userInDB);
	}

	// @GetMapping("/users")
	// public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> getAllUsers(
	// 				@RequestParam int page,
	// 				@RequestParam int size) {	
	// 	Pageable pageable = PageRequest.of(page, size);
	// 	Page<UserResponseDTO> userResponseDTOs = this.userService.fetchUsers(pageable);
	// 	return ApiResponse.success(PageResponse.from(userResponseDTOs));
	// }

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> getAllUsers(Pageable pageable) {	
		Page<UserResponseDTO> userResponseDTOs = this.userService.fetchUsers(pageable);
		return ApiResponse.success(PageResponse.from(userResponseDTOs));
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable int id) {
		UserResponseDTO userResponseDTO = this.userService.fetchUserById(id);
		return ApiResponse.success(userResponseDTO);
	}

	@PutMapping("/users/update/{id}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserById(@PathVariable int id,
			@RequestBody UserRequestDTO inputUser) {

		UserResponseDTO userResponseDTO = this.userService.updateUserById(id, inputUser);

		return ApiResponse.success(userResponseDTO);
	}

	@DeleteMapping("/users/delete/{id}")
	public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable int id) {
		this.userService.deleteUserById(id);
		return ApiResponse.success("delete success");
	}
}
