package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentDTO;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentResponseDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskFile;
import sit.int221.servicetasksj3.exceptions.ErrorDetails;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.FileRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;

import java.io.IOException;
import java.util.ArrayList;
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

    private static final List<String> PREVIEW_FILE_TYPES = List.of(
            "image/png",
            "image/jpeg",
            "text/plain",
            "application/rtf",
            "application/pdf"
    );

    private boolean isPreviewFileType(String fileType) {
        return fileType != null && PREVIEW_FILE_TYPES.contains(fileType.toLowerCase());
    }

    // Get all attachments of a task
    public List<AttachmentDTO> getAttachments(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));
        return task.getFiles().stream()
                .map(file -> new AttachmentDTO(file.getFileId(), file.getFileName(), file.getFileType(), file.getFileData(), file.getUploadDate(), isPreviewFileType(file.getFileType())))
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
                .header(HttpHeaders.CONTENT_DISPOSITION, isPreviewFileType(fileType) ?
                        "inline; filename=\"" + taskFile.getFileName() + "\"" :
                        "attachment; filename=\"" + taskFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileType != null ? fileType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
    }

    // Upload attachments to a task
    public AttachmentResponseDTO addAttachments(Integer taskId, List<MultipartFile> files) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));

        List<AttachmentDTO> successfulUploads = new ArrayList<>();
        List<ErrorDetails.ValidationError> errorList = new ArrayList<>();

        int remainingSlots = MAX_FILES - task.getFiles().size();
        if (files.size() > remainingSlots) {
            errorList.add(new ErrorDetails.ValidationError("files", "Each task can have at most " + MAX_FILES + " files."));
            files = files.subList(0, remainingSlots);
        }

        for (MultipartFile file : files) {
            try {
                String fileName = file.getOriginalFilename();
                String fileType = file.getContentType();

                if (!isValidFileSize(file)) {
                    errorList.add(new ErrorDetails.ValidationError("files", fileName + " exceeds max file size of " + MAX_FILE_SIZE_MB + " MB."));
                    continue; // skip file with invalid size
                }

                if (isDuplicateFile(task, fileName)) {
                    errorList.add(new ErrorDetails.ValidationError("files", "File with the same filename cannot be added: " + fileName));
                    continue; // skip file with duplicate name
                }

                TaskFile taskFile = new TaskFile();
                taskFile.setTask(task);
                taskFile.setFileName(fileName);
                taskFile.setFileType(fileType);
                taskFile.setFileData(file.getBytes());
                fileRepository.save(taskFile);
                task.addFile(taskFile);

                successfulUploads.add(new AttachmentDTO(
                        taskFile.getFileId(),
                        taskFile.getFileName(),
                        taskFile.getFileType(),
                        taskFile.getFileData(),
                        taskFile.getUploadDate(),
                        isPreviewFileType(taskFile.getFileType())
                ));
            } catch (IOException e) {
                errorList.add(new ErrorDetails.ValidationError("files", "Error uploading file: " + file.getOriginalFilename()));
            }
        }

        String message = errorList.isEmpty()
                ? "All files uploaded successfully."
                : "Some files could not be uploaded.";
        return new AttachmentResponseDTO(message, successfulUploads, errorList);
    }

//    public AttachmentResponseDTO addAttachments(Integer taskId, List<MultipartFile> files) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ItemNotFoundException("Task not found"));
//
//        ValidationException validationError = new ValidationException("Validation error");
//        List<AttachmentDTO> successfulUploads = new ArrayList<>();
//
//        int remainingSlots = MAX_FILES - task.getFiles().size();
//        if (files.size() > remainingSlots) {
//            validationError.addValidationError("files", "Each task can have at most " + MAX_FILES + " files.");
//            files = files.subList(0, remainingSlots);
//        }
//
//        for (MultipartFile file : files) {
//            try {
//                String fileName = file.getOriginalFilename();
//                String fileType = file.getContentType();
//
//                // ตรวจสอบขนาดไฟล์
//                if (!isValidFileSize(file)) {
//                    validationError.addValidationError("files", fileName + " exceeds max file size of " + MAX_FILE_SIZE_MB + " MB.");
//                    continue; // skip file with invalid size
//                }
//
//                if (isDuplicateFile(task, fileName)) {
//                    validationError.addValidationError("files", "File with the same filename cannot be added: " + fileName);
//                    continue; // skip file with duplicate name
//                }
//
//                TaskFile taskFile = new TaskFile();
//                taskFile.setTask(task);
//                taskFile.setFileName(fileName);
//                taskFile.setFileType(fileType);
//                taskFile.setFileData(file.getBytes());
//                fileRepository.save(taskFile);
//                task.addFile(taskFile);
//
//                successfulUploads.add(new AttachmentDTO(
//                        taskFile.getFileId(),
//                        taskFile.getFileName(),
//                        taskFile.getFileType(),
//                        taskFile.getFileData(),
//                        taskFile.getUploadDate(),
//                        isPreviewFileType(taskFile.getFileType())
//                ));
//            } catch (IOException e) {
//                validationError.addValidationError("files", "Error uploading file: " + file.getOriginalFilename());
//            }
//        }
//
//        String message = validationError.getErrors().isEmpty()
//                ? "All files uploaded successfully."
//                : "Some files could not be uploaded.";
//
//        return new AttachmentResponseDTO(message, successfulUploads, validationError.getErrors());
//    }


    // Delete an attachment
    public AttachmentDTO deleteAttachment(Integer attachmentId) {
        TaskFile taskFile = fileRepository.findById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException("Attachment not found"));

        AttachmentDTO deletedAttachmentDTO = new AttachmentDTO(
                taskFile.getFileId(),
                taskFile.getFileName(),
                taskFile.getFileType(),
                taskFile.getFileData(),
                taskFile.getUploadDate(),
                isPreviewFileType(taskFile.getFileType())
        );

        fileRepository.delete(taskFile);
        return deletedAttachmentDTO;
    }
}

