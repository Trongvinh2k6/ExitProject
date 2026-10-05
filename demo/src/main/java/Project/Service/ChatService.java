package Project.Service;

import Project.Model.DTO.ChatResponseDTO;
import Project.Model.DTO.ProductResponseDTO;
import Project.Model.DTO.ProductSearchDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {

    private final ProductService productService;
    private final GeminiService geminiService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ChatResponseDTO chat(String message) {

        // 1. Lấy danh sách category và brand hiện tại từ database
        List<String> categories =
                categoryService.getCategoryNames();

        List<String> brands =
                brandService.getBrandNames();

        // 2. Gemini phân tích câu hỏi thành điều kiện tìm kiếm
        ProductSearchDTO criteria =
                geminiService.extractProductSearch(
                        message,
                        categories,
                        brands
                );

        // 3. Dùng điều kiện Gemini trả về để tìm sản phẩm trong database
        List<ProductResponseDTO> products =
                productService.searchProducts(
                        criteria.getKeyword(),
                        criteria.getBrand(),
                        criteria.getMinPrice(),
                        criteria.getMaxPrice(),
                        criteria.getCategory()
                );

        // 4. Chuyển danh sách sản phẩm thành context cho Gemini
        StringBuilder productInfo = new StringBuilder();

        for (ProductResponseDTO product : products) {

            productInfo.append("- ")
                    .append(product.getName())
                    .append(" - ")
                    .append(product.getPrice())
                    .append(" VND\n");
        }

        // 5. Gemini tạo câu trả lời tự nhiên
        String prompt = """
                Bạn là chatbot của cửa hàng giày ProjectSkateBoard.

                Khách hàng hỏi:

                %s

                Các sản phẩm tìm được trong database:

                %s

                Hãy tư vấn cho khách hàng dựa trên danh sách sản phẩm trên.

                Không được tự tạo ra sản phẩm không có trong danh sách.

                Nếu danh sách sản phẩm trống,
                hãy nói rằng hiện tại không tìm thấy sản phẩm phù hợp.

                Trả lời bằng tiếng Việt.
                """.formatted(message, productInfo);

        String reply = geminiService.chat(prompt);

        // 6. Trả cả câu trả lời + danh sách sản phẩm về frontend
        return new ChatResponseDTO(reply, products);
    }
}