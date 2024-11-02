package sit.int221.servicetasksj3.dtos.collaboratorDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDTO {
    private String id;
    private String name;
    private String email;
    private String action;
}
