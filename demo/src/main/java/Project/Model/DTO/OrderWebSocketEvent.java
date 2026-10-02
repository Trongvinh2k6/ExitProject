package Project.Model.DTO;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@NoArgsConstructor 
public class OrderWebSocketEvent {
    private String type;
    private Object data;
}
