package Project.Service;

import org.springframework.stereotype.Service;

import Project.Model.DTO.ProductSearchDTO;

@Service
public class ProductSearchParser {
     public ProductSearchDTO parse(String message) {

        String keyword = null;
        String brand = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        String category = null;

        String lowerMessage = message.toLowerCase();

        // Tạm thời nhận diện brand
        if (lowerMessage.contains("nike")) {
            brand = "Nike";
        } else if (lowerMessage.contains("adidas")) {
            brand = "Adidas";
        } else if (lowerMessage.contains("puma")) {
            brand = "Puma";
        }

        // Nhận diện category
        if (lowerMessage.contains("chạy bộ")) {
            category = "Running";
        } else if (lowerMessage.contains("bóng rổ")) {
            category = "Basketball";
        } else if (lowerMessage.contains("đi chơi")) {
            category = "Lifestyte";
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
        else if (lowerMessage.contains("trên")) {

            String[] parts = lowerMessage.split("trên");

            if (parts.length > 1) {

                String pricePart = parts[1]
                        .replace("triệu", "")
                        .trim();

                try {
                    double price = Double.parseDouble(pricePart);

                    minPrice = (int) (price * 1_000_000);

                } catch (NumberFormatException e) {
                    // Không lấy được giá
                }
            }
        }
        else if (lowerMessage.contains("từ") && lowerMessage.contains("đến")) {

            String[] parts = lowerMessage.split("từ");

            if (parts.length > 1) {

                String pricePart = parts[1];

                String[] prices = pricePart.split("đến");

                if (prices.length > 1) {

                    try {
                        double min = Double.parseDouble(
                                prices[0]
                                        .replace("triệu", "")
                                        .trim()
                        );

                        double max = Double.parseDouble(
                                prices[1]
                                        .replace("triệu", "")
                                        .trim()
                        );

                        minPrice = (int) (min * 1_000_000);
                        maxPrice = (int) (max * 1_000_000);

                    } catch (NumberFormatException e) {
                        // Không lấy được giá
                    }
                }
            }
        }
        else if (lowerMessage.contains("khoảng")) {

            String[] parts = lowerMessage.split("khoảng");

            if (parts.length > 1) {

                String pricePart = parts[1]
                        .replace("triệu", "")
                        .trim();

                try {
                    double price = Double.parseDouble(pricePart);

                    int priceInVND = (int) (price * 1_000_000);

                    minPrice = priceInVND - 500_000;
                    maxPrice = priceInVND + 500_000;

                } catch (NumberFormatException e) {
                    // Không lấy được giá
                }
            }
        }

        return new ProductSearchDTO(
                keyword,
                brand,
                minPrice,
                maxPrice,
                category
        );
    }
}
