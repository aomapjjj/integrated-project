package sit.int221.servicetasksj3.dtos.filesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.servicetasksj3.exceptions.ErrorDetails;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponseDTO {
    private String message;
    private List<AttachmentDTO> successfulUploads;
    List<ErrorDetails.ValidationError> errors;
}
