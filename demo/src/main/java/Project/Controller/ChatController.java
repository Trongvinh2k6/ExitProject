package Project.Controller;

import org.springframework.web.bind.annotation.*;

import Project.Service.GeminiService;

@RestController
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return geminiService.chat(message);
    }
}
