package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentDTO;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentResponseDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskFile;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.FileRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    @Value("${file.max-files-per-task}")
    private int MAX_FILES;

    @Value("${file.max-file-size}")
    private int MAX_FILE_SIZE_MB;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TaskRepository taskRepository;

    private boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= (long) MAX_FILE_SIZE_MB * 1024 * 1024;
    }
    private boolean isDuplicateFile(Task task, String fileName) {
        return task.getFiles().stream().anyMatch(existingFile -> existingFile.getFileName().equals(fileName));
    }

    // Get all attachments of a task
    public List<AttachmentDTO> getAttachments(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));
        return task.getFiles().stream()
                .map(file -> new AttachmentDTO(file.getFileId(), file.getFileName(), file.getFileType(), file.getFileData(), file.getUploadDate()))
                .collect(Collectors.toList());
    }

    // Download an attachment
    public ResponseEntity<Resource> downloadAttachment(Integer taskId, String filename) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));

        TaskFile taskFile = task.getFiles().stream()
                .filter(file -> file.getFileName().equals(filename))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Attachment not found"));

        String fileType = taskFile.getFileType();
        Resource resource = new ByteArrayResource(taskFile.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + taskFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileType != null ? fileType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
    }

    // Upload attachments to a task
    public AttachmentResponseDTO addAttachments(Integer taskId, List<MultipartFile> files) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));

        if (task.getFiles().size() + files.size() > MAX_FILES) {
            throw new ValidationException("Each task can have at most " + MAX_FILES + " files.");
        }

        List<AttachmentDTO> attachmentDTOs = files.stream().map(file -> {
            try {
                String fileName = file.getOriginalFilename();
                String fileType = file.getContentType();

                if (!isValidFileSize(file)) {
                    throw new ValidationException(fileName + " exceeds max file size of " + MAX_FILE_SIZE_MB + " MB.");
                }
                if (isDuplicateFile(task, fileName)) {
                    throw new ValidationException("File with the same filename cannot be added: " + fileName);
                }

                TaskFile taskFile = new TaskFile();
                taskFile.setTask(task);
                taskFile.setFileName(file.getOriginalFilename());
                taskFile.setFileType(fileType);
                taskFile.setFileData(file.getBytes());
                fileRepository.save(taskFile);
                task.addFile(taskFile);

                return new AttachmentDTO(taskFile.getFileId(), taskFile.getFileName(), taskFile.getFileType(), taskFile.getFileData(), taskFile.getUploadDate());
            } catch (IOException e) {
                throw new ValidationException("Error uploading file: " + file.getOriginalFilename());
            }
        }).collect(Collectors.toList());
        return new AttachmentResponseDTO("Files uploaded successfully.", attachmentDTOs);
    }

    // Delete an attachment
//    public AttachmentDTO deleteAttachment(Integer attachmentId) {
//        TaskFile taskFile = fileRepository.findById(attachmentId)
//                .orElseThrow(() -> new ItemNotFoundException("Attachment not found"));
//
//        AttachmentDTO deletedAttachmentDTO = new AttachmentDTO(
//                taskFile.getFileId(),
//                taskFile.getFileName(),
//                taskFile.getUploadDate()
//        );
//
//        fileRepository.delete(taskFile);
//        return deletedAttachmentDTO;
//    }
}

