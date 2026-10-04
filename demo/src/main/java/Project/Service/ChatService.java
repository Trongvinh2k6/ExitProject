package Project.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import Project.Model.DTO.ProductResponseDTO;
import Project.Model.DTO.ProductSearchDTO;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
public class ChatService {

    private final ProductService productService;
    private final GeminiService geminiService;
    private final ProductSearchParser productSearchParser;


    public String chat(String message) {

        ProductSearchDTO criteria =
            productSearchParser.parse(message);

        List<ProductResponseDTO> products =
                productService.searchProducts(
                        criteria.getKeyword(),
                        criteria.getMinPrice(),
                        criteria.getMaxPrice()
                );


        StringBuilder productInfo = new StringBuilder();

        for (ProductResponseDTO product : products) {
            productInfo.append(
                    "- " + product.getName()
                    + " - " + product.getPrice()
                    + "\n"
            );
        }

        String prompt = """
                Bạn là chatbot của cửa hàng giày ProjectSkateBoard.

                Khách hàng hỏi:
                %s

                Các sản phẩm tìm được trong database:
                %s

                Hãy tư vấn cho khách hàng dựa trên danh sách sản phẩm trên.
                Không được tự tạo ra sản phẩm không có trong danh sách.
                """.formatted(
                        message,
                        productInfo
                );

        return geminiService.chat(prompt);
    }
}
