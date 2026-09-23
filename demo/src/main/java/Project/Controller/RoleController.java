package Project.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Model.Role;
import Project.Model.DTO.RoleResponseDTO;
import Project.Service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@PostMapping("/roles/create")
	public ResponseEntity<ApiResponse<Role>> postRole(@Valid @RequestBody Role inputRole) {
		Role role = this.roleService.createRole(inputRole);
		return ApiResponse.created(role);
	}

	@GetMapping("/roles")
	public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getRoles() {
		List<RoleResponseDTO> roles = this.roleService.fetchRoles();
		return ApiResponse.success(roles);
	}

	@GetMapping("/roles/{id}")
	public ResponseEntity<ApiResponse<RoleResponseDTO>> getRole(@PathVariable int id) {
		RoleResponseDTO role = this.roleService.fetchRoleById(id);
		return ApiResponse.success(role);
	}

	@PutMapping("/roles/update/{id}")
	public ResponseEntity<ApiResponse<RoleResponseDTO>> putRole(@PathVariable int id, @Valid @RequestBody Role inputRole) {
		RoleResponseDTO role = this.roleService.updateRoleById(id, inputRole);
		return ApiResponse.success(role);
	}

	@DeleteMapping("/roles/delete/{id}")
	public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable int id) {
		this.roleService.deleteRoleById(id);
		return ApiResponse.success("ok");
	}
}