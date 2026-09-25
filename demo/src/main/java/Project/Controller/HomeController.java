package Project.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Dùng @Controller thay vì @RestController để chuyển trang/view
public class HomeController {

    // Khi người dùng gõ "/" hoặc "/home", đều hiển thị trang home
    // @GetMapping("/home")
    // public String home() {
    //     return "forward:/index.html"; // Giữ nguyên URL /home nhưng tải nội dung index.html
    // }
}