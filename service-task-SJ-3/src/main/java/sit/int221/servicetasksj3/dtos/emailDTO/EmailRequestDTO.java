package sit.int221.servicetasksj3.dtos.emailDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    private String boardId;
    private String email;
    private String inviterName;
    private String boardName;
    private String accessRight;
//    private String boardUrl;
}