package Project.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(Client client) {
        this.client = client;
    }

    public String chat(String message) {

        GenerateContentResponse response =
            client.models.generateContent(
                    "gemini-3.8-flash",
                    message,
                    null
            );

        return response.text();
    }
}