package sit.int221.servicetasksj3.dtos.boardsDTO;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoBoardsResponseDTO {
    private List<Object> collab = Collections.emptyList();
}
