package Project.Controller;

import org.springframework.web.bind.annotation.*;

import Project.Model.DTO.ChatRequestDTO;
import Project.Model.DTO.ChatResponseDTO;
import Project.Service.ChatService;
import Project.Service.GeminiService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@AllArgsConstructor 
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {
        return chatService.chat(request.getMessage());
    }
}
