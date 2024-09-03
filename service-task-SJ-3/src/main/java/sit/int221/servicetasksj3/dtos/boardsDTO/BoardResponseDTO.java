package sit.int221.servicetasksj3.dtos.boardsDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDTO {
    private String id;
    private String name;
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
}