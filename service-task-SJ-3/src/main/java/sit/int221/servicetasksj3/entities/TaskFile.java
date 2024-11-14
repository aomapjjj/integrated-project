package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "taskfiles")
public class TaskFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] fileData;
    private LocalDateTime uploadDate = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}
