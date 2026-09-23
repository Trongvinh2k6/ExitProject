package Project.Model.DTO;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
	private Integer id;

	private String content;

	private boolean isApproved;

	private Instant createdAt;

	private Instant updatedAt;

	private int userId;

	private int productId;
}
