package Project.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Comment;
import Project.Model.DTO.CommentResponseDTO;
import Project.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;


    // Convert Comment -> CommentResponseDTO
    public CommentResponseDTO convertCommentToDTO(Comment comment) {

        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser().getId());
        dto.setProductId(comment.getProduct().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setApproved(comment.isApproved());

        return dto;
    }

    // Create comment
    public Comment createComment(Comment comment) {
		comment.setApproved(true);
		return this.commentRepository.save(comment);
	}

    // Get all comments
    public List<CommentResponseDTO> fetchComments() {

        List<Comment> comments = this.commentRepository.findAll();

        return comments.stream()
                .map(this::convertCommentToDTO)
                .toList();
    }

    // Get comment by ID
    public CommentResponseDTO fetchCommentById(Integer id) {

        Comment comment = this.commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment not found"));

        return convertCommentToDTO(comment);
    }

    // Update comment
    public CommentResponseDTO updateCommentById(
            Integer id,
            Comment inputComment) {

        Comment comment = this.commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment not found"));

        comment.setContent(inputComment.getContent());
        comment.setApproved(inputComment.isApproved());

        Comment updatedComment = this.commentRepository.save(comment);

        return convertCommentToDTO(updatedComment);
    }

    // Delete comment
    public void deleteCommentById(Integer id) {

        Comment comment = this.commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment not found"));

        this.commentRepository.delete(comment);
    }
}