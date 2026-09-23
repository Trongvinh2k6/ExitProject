package Project.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceAlreadyExistsException;
import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Role;
import Project.Model.DTO.RoleResponseDTO;
import Project.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role createRole(Role inputRole) {
        if(this.roleRepository.existsByName(inputRole.getName())) {
            throw new ResourceAlreadyExistsException("Role với name = " + inputRole.getName() + " đã tồn tại");
        }
        return this.roleRepository.save(inputRole);
    }

    public List<RoleResponseDTO> fetchRoles() {
        List<RoleResponseDTO> roleList = this.roleRepository.findAll().stream()
					.map(role -> RoleResponseDTO.builder().id(role.getId()).name(role.getName()).description(role.getDescription()).build())
					.collect(Collectors.toList());
		return roleList;
    }

    public RoleResponseDTO fetchRoleById(int id) {
        Role roleInDB = this.roleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại role với id = " + id));
		return RoleResponseDTO.builder().id(roleInDB.getId()).name(roleInDB.getName()).build();
    }

    public RoleResponseDTO updateRoleById(int id, Role updateRole) {
		Role roleInDB = this.roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tồn tại role với id = " + id));

		// check role's name là duy nhất
		if (roleInDB.getName().equals(updateRole.getName())) {
			roleInDB.setDescription(updateRole.getDescription());
		} else {
			if (this.roleRepository.existsByNameAndIdNot(updateRole.getName(), id)) {
				throw new ResourceAlreadyExistsException("Role với name = " + updateRole.getName() + " đã tồn tại");
			}
			roleInDB.setName(updateRole.getName());
            roleInDB.setDescription(updateRole.getDescription());
		}

		Role savedRole = this.roleRepository.save(roleInDB);

		RoleResponseDTO response = new RoleResponseDTO();
		response.setName(savedRole.getName());
		response.setDescription(savedRole.getDescription());

    	return response;
	}

	public void deleteRoleById(int id) {
		this.roleRepository.deleteById(id);
	}
}
