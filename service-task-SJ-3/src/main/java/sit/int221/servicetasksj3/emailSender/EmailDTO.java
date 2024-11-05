package sit.int221.servicetasksj3.emailSender;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String boardId;
    private String email;
    private String inviterName;
    private String boardName;
    private String accessRight;
    private String boardUrl;
}