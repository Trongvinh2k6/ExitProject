package Project.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ProductWebSocketEvent {

    private String type;
    private Object data;
}