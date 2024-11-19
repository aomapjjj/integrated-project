package sit.int221.servicetasksj3.controller;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentDTO;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentResponseDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.SimpleTaskDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskDTOTwo;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskNewDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.services.FileService;
import sit.int221.servicetasksj3.services.TaskService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class TaskController {
    @Autowired
    private TaskService service;

    @Autowired
    private FileService fileService;

    // ----------------------- File -----------------------
    // Get File
    @GetMapping("{taskId}/attachments")
    public ResponseEntity<List<AttachmentDTO>> getAttachments(@PathVariable Integer taskId) {
        List<AttachmentDTO> attachments = fileService.getAttachments(taskId);
        return ResponseEntity.ok(attachments);
    }
    // Download File
    @GetMapping("{taskId}/attachments/{filename:.+}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Integer taskId, @PathVariable String filename) {
        return fileService.downloadAttachment(taskId, filename);
    }
    // Upload File
    @PostMapping("{taskId}/attachments")
    public ResponseEntity<AttachmentResponseDTO> addAttachments(@PathVariable Integer taskId, @RequestParam("files") List<MultipartFile> files) throws IOException {
        AttachmentResponseDTO response = fileService.addAttachments(taskId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // Delete File
    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<AttachmentDTO> deleteAttachment(@PathVariable Integer taskId, @PathVariable Integer attachmentId) {
        AttachmentDTO deletedAttachment = fileService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(deletedAttachment);
    }

    //GET ALL TASKS V.2
    @GetMapping("")
    public List<TaskNewDTO> getAllTasksFilteredV2(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String[] filterStatuses
    ) {
        return service.getAllTasksFilteredV2(sortBy, filterStatuses);
    }
//
//    @GetMapping("/{id}")
//    public SimpleTaskDTO getTaskById(@PathVariable Integer id) {
//        Task task = service.findByID(id);
//        SimpleTaskDTO simpleTaskDTO = modelMapper.map(task, SimpleTaskDTO.class);
//        return simpleTaskDTO;
//    }
//
//    // ADD
//    @PostMapping("")
//    public ResponseEntity<TaskDTOTwo> createNewTasks(@Valid @RequestBody TaskNewDTO task) {
//        Task createTask = service.createNewTasks(task);
//        TaskDTOTwo createdTaskDTO = modelMapper.map(createTask, TaskDTOTwo.class);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
//    }
//
//    // DELETE
//    @DeleteMapping("/{id}")
//    public ResponseEntity<TaskDTO> removeTasks(@PathVariable Integer id) {
//        TaskDTO deletedTaskDTO = service.removeTasks(id);
//        return ResponseEntity.ok().body(deletedTaskDTO);
//    }
//
//    // EDIT
//    @PutMapping("/{id}")
//    public ResponseEntity<TaskDTOTwo> updateTasks(@PathVariable Integer id, @Valid @RequestBody TaskNewDTO task) {
//        Task updatedTask = service.updateTask(id, task);
//        TaskDTOTwo updatedTaskDTO = modelMapper.map(updatedTask, TaskDTOTwo.class);
//        return ResponseEntity.ok(updatedTaskDTO);
//    }
}
