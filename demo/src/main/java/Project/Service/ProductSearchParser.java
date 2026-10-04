package Project.Service;

import org.springframework.stereotype.Service;

import Project.Model.DTO.ProductSearchDTO;

@Service
public class ProductSearchParser {
     public ProductSearchDTO parse(String message) {

        String keyword = null;
        Integer minPrice = null;
        Integer maxPrice = null;

        String lowerMessage = message.toLowerCase();

        // Tạm thời nhận diện brand
        if (lowerMessage.contains("nike")) {
            keyword = "Nike";
        } else if (lowerMessage.contains("adidas")) {
            keyword = "Adidas";
        } else if (lowerMessage.contains("puma")) {
            keyword = "Puma";
        }

        // Xử lý "dưới X triệu"
        if (lowerMessage.contains("dưới")) {

            String[] parts = lowerMessage.split("dưới");

            if (parts.length > 1) {
                String pricePart = parts[1]
                        .replace("triệu", "")
                        .trim();

                try {
                    double price = Double.parseDouble(pricePart);
                    maxPrice = (int) (price * 1_000_000);
                } catch (NumberFormatException e) {
                    // Không lấy được giá
                }
            }
        }

        return new ProductSearchDTO(
                keyword,
                minPrice,
                maxPrice
        );
    }
}
