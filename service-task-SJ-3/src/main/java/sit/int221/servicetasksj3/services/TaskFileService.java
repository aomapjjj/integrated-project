package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskFile;
import sit.int221.servicetasksj3.repositories.TaskFileRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskFileService {

    @Value("${attachment.max-files}")
    private int MAX_FILES;

    @Value("${attachment.max-file-size}")
    private int MAX_FILE_SIZE_MB;

    @Autowired
    private TaskFileRepository taskFileRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskFile> getAttachments(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return task.getFiles();
    }


    public List<String> addAttachments(Integer taskId, List<MultipartFile> files) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Check if adding files exceeds max file limit
        if (task.getFiles().size() + files.size() > MAX_FILES) {
            errorMessages.add("Each task can have at most " + MAX_FILES + " files.");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            // Check for file size and uniqueness
            if (!isValidFileSize(file)) {
                errorMessages.add(fileName + " exceeds max file size of " + MAX_FILE_SIZE_MB + " MB.");
                continue;
            }
            if (isDuplicateFile(task, fileName)) {
                errorMessages.add("File with the same filename cannot be added: " + fileName);
                continue;
            }

            // Save valid files
            TaskFile taskFile = new TaskFile();
            taskFile.setTask(task);
            taskFile.setFileName(fileName);
            taskFile.setFileData(file.getBytes());
            taskFileRepository.save(taskFile);
            task.addFile(taskFile);
        }

        return errorMessages;
    }

    private boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= (long) MAX_FILE_SIZE_MB * 1024 * 1024;
    }

    private boolean isDuplicateFile(Task task, String fileName) {
        return task.getFiles().stream().anyMatch(existingFile -> existingFile.getFileName().equals(fileName));
    }

    public void deleteAttachment(Long attachmentId) {
        if (taskFileRepository.existsById(attachmentId)) {
            taskFileRepository.deleteById(attachmentId);
        } else {
            throw new ResourceNotFoundException("Attachment not found");
        }
    }
}
