package sit.int221.servicetasksj3.dtos.filesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Integer fileId;
    private String fileName;
    private String fileType;
    private byte[] fileData;
    private LocalDateTime uploadDate;
}
