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
import sit.int221.servicetasksj3.exceptions.ErrorDetails;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
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
            "image/gif",
            "image/bmp",
            "image/svg+xml",
            "image/webp",
            "text/plain",
            "text/csv",
            "application/json",
            "application/xml",
            "application/pdf",
            "application/rtf",
            "video/mp4",
            "video/webm",
            "video/ogg",
            "audio/mpeg",
            "audio/wav",
            "audio/ogg"
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
        List<String> notAddedFiles = new ArrayList<>();

        int existingFileCount = task.getFiles().size();
        int remainingSlots = MAX_FILES - existingFileCount;

        if (files.size() > remainingSlots) {
            errorList.add(new ErrorDetails.ValidationError(
                    "files",
                    "Each task can have at most " + MAX_FILES + " files."
            ));
            notAddedFiles.addAll(
                    files.subList(remainingSlots, files.size()).stream()
                            .map(MultipartFile::getOriginalFilename)
                            .collect(Collectors.toList())
            );
            files = files.subList(0, remainingSlots); // limit files to remaining slots
        }

        long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            long fileSize = file.getSize();

            if (fileSize > MAX_FILE_SIZE_BYTES) {
                errorList.add(new ErrorDetails.ValidationError(
                        "files",
                        String.format("%s exceeds max file size of %d MB (%.2f MB detected).",
                                fileName, MAX_FILE_SIZE_MB, fileSize / (1024.0 * 1024.0))
                ));
                notAddedFiles.add(fileName);
                continue; // skip file with invalid size
            }

            if (isDuplicateFile(task, fileName)) {
                errorList.add(new ErrorDetails.ValidationError(
                        "files",
                        "File with the same filename cannot be added or updated to the attachments. Please delete the attachment and add it again to update the file: " + fileName
                ));
                notAddedFiles.add(fileName);
                continue; // skip file with duplicate name
            }

            try {
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
                errorList.add(new ErrorDetails.ValidationError(
                        "files",
                        "Error uploading file: " + fileName
                ));
                notAddedFiles.add(fileName);
            }
        }

        StringBuilder message = new StringBuilder();
        if (!errorList.isEmpty()) {
            message.append("- Some files could not be uploaded due to the following errors:\n");
            for (ErrorDetails.ValidationError error : errorList) {
                message.append("  - ").append(error.getMessage()).append("\n");
            }
        }
        if (!notAddedFiles.isEmpty()) {
            message.append("The following files were not added: ").append(String.join(", ", notAddedFiles)).append(".");
        }
        if (errorList.isEmpty()) {
            message.insert(0, "All files uploaded successfully.");
        }

        return new AttachmentResponseDTO(message.toString(), successfulUploads, errorList);
    }

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