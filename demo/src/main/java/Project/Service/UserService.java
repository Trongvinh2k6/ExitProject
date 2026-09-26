package Project.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceAlreadyExistsException;
import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Cart;
import Project.Model.Role;
import Project.Model.User;
import Project.Model.DTO.RoleResponseDTO;
import Project.Model.DTO.UserRequestDTO;
import Project.Model.DTO.UserResponseDTO;
import Project.Repository.RoleRepository;
import Project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public User convertDTOToUser(UserRequestDTO userRequestDTO) {
		User user = new User();
		user.setName(userRequestDTO.getName());
		user.setEmail(userRequestDTO.getEmail());
		user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
		user.setPhone(userRequestDTO.getPhone());
		user.setAddress(userRequestDTO.getAddress());
		//user.setRole(userRequestDTO.getRole());

		return user;

	}

	public UserResponseDTO convertUserToDTO(User user) {
		UserResponseDTO userResponseDTO = new UserResponseDTO();
		userResponseDTO.setId(user.getId());
		userResponseDTO.setName(user.getName());
		userResponseDTO.setEmail(user.getEmail());
		userResponseDTO.setAddress(user.getAddress());
		userResponseDTO.setPhone(user.getPhone());

		RoleResponseDTO userRole = new RoleResponseDTO(user.getRole().getId(), user.getRole().getName(), user.getRole().getDescription());
		userResponseDTO.setRoleResponseDTO(userRole);

		return userResponseDTO;
	}

	public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
		if (this.userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new ResourceAlreadyExistsException("Email đã tồn tại: " + userRequestDTO.getEmail());
		}
		User user = convertDTOToUser(userRequestDTO);

		Cart cart = new Cart();

		user.setCart(cart);
		cart.setUser(user);

		String roleName = "USER";
        if (userRequestDTO.getEmail().equals("daotrongvinha1k63tp@gmail.com")) {
            roleName = "ADMIN";
        }

		final String finalRoleName = roleName;
        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + finalRoleName));
        user.setRole(userRole);

		return convertUserToDTO(this.userRepository.save(user));
	}

	public Page<UserResponseDTO> fetchUsers(Pageable pageable) {
		Page<UserResponseDTO> userResponseDTOs = this.userRepository.findAll(pageable)
																	.map(user -> {return convertUserToDTO(user);});
		return userResponseDTOs;
	}

	public UserResponseDTO fetchUserById(int id) {
		User user = this.userRepository.findById(id)
												.orElseThrow(() -> new ResourceNotFoundException("Khong tim thay id"));
		return convertUserToDTO(user);
	}

	public UserResponseDTO updateUserById(int id, UserRequestDTO userRequestDTO) {
		User user = this.userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("user not found with id = " + id));
		user.setName(userRequestDTO.getName());
		user.setEmail(userRequestDTO.getEmail());
		//user.setPassword(userRequestDTO.getPassword());
		user.setPhone(userRequestDTO.getPhone());
		user.setAddress(userRequestDTO.getAddress());
		//user.setRole(userRequestDTO.getRole());
		return convertUserToDTO(this.userRepository.save(user));
	}

	public void deleteUserById(int id) {
		this.userRepository.deleteById(id);
	}

	public User findUserByEmail(String email) {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Email not found from service"));
	}
}
