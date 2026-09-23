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
import Project.Model.Comment;
import Project.Model.DTO.CommentResponseDTO;
import Project.Service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/comments/create")
	public ResponseEntity<ApiResponse<Comment>> createComment(@Valid @RequestBody Comment inputComment) {
		Comment comment = this.commentService.createComment(inputComment);
		return ApiResponse.created(comment);
	}

	@GetMapping("/comments")
	public ResponseEntity<?> getComments() {
		List<CommentResponseDTO> comments = this.commentService.fetchComments();
		return ApiResponse.success(comments);
	}

	@GetMapping("/comments/{id}")
	public ResponseEntity<ApiResponse<CommentResponseDTO>> getCommentById(@PathVariable int id) {
		CommentResponseDTO comment = this.commentService.fetchCommentById(id);
		return ApiResponse.success(comment);
	}

	@PutMapping("/comments/update/{id}")
	public ResponseEntity<ApiResponse<CommentResponseDTO>> updateCommentById(@PathVariable int id,
			@Valid @RequestBody Comment inputComment) {
		CommentResponseDTO comment = this.commentService.updateCommentById(id, inputComment);
		return ApiResponse.success(comment);
	}

	@DeleteMapping("/comments/delete/{id}")
	public ResponseEntity<ApiResponse<String>> deleteCommentById(@PathVariable int id) {
		this.commentService.deleteCommentById(id);
		return ApiResponse.success("ok");
	}
}
