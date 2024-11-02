package sit.int221.servicetasksj3.dtos.boardsDTO;

import lombok.*;
import sit.int221.servicetasksj3.dtos.collaboratorDTO.CollaboratorDTO;
import sit.int221.servicetasksj3.entities.Visibility;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDTO{
    private String id;
    private String name;
    private Visibility visibility;
    private OwnerDTO owner;

    // Inner class for Owner
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerDTO {
        private String oid;
        private String name;
    }

    private List<CollaboratorDTO> collaborators;
}