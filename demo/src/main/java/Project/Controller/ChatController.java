package Project.Controller;

import org.springframework.web.bind.annotation.*;

import Project.Service.ChatService;
import Project.Service.GeminiService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@AllArgsConstructor 
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatService.chat(message);
    }
}
