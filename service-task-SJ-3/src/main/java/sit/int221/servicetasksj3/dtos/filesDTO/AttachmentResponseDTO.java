package sit.int221.servicetasksj3.dtos.filesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponseDTO {
    private String message;
    private List<AttachmentDTO> attachments;
}
