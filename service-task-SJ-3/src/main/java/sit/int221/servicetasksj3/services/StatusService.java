package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.servicetasksj3.dtos.StatusDTO;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    @Autowired
    private StatusRepository repository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<StatusDTO> getAllStatuses() {
        return listMapper.mapList(repository.findAll(), StatusDTO.class, modelMapper);
    }

    public TaskStatus getStatusesById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // ADD
    @Transactional
    public List<StatusDTO> createNewStatuses(StatusDTO statusDTO) {
        if (statusDTO.getName() == null || statusDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Status name is required");
        }
        if (statusDTO.getName().trim().length() > 50) {
            throw new RuntimeException("Status name cannot exceed 50 characters");
        }
//        if (statusDTO.getDescription().trim().isEmpty()) { // NOT EMPTY
//            throw new RuntimeException("Status description is not empty");
//        }
        if (statusDTO.getDescription().trim().length() > 200) {
            throw new RuntimeException("Status description cannot exceed 200 characters");
        }
        // ตรวจสอบว่าสถานะที่กำลังสร้างนั้นมีชื่อซ้ำกับสถานะที่มีอยู่แล้วหรือไม่
        List<TaskStatus> existingStatuses = repository.findByName(statusDTO.getName().trim());
        if (!existingStatuses.isEmpty()) {
            throw new RuntimeException("Status with name '" + statusDTO.getName().trim() + "' already exists");
        }
        try {
            // Create TaskStatus entity
            TaskStatus status = new TaskStatus();
                status.setName(statusDTO.getName().trim());
                status.setDescription(statusDTO.getDescription().trim());
            // Save TaskStatus entity
            TaskStatus savedStatus = repository.save(status);
            // Map the saved TaskStatus entity to StatusDTO
            return Collections.singletonList(modelMapper.map(savedStatus, StatusDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task status to the database", e);
        }
    }

    // EDIT
    @Transactional
    public TaskStatus updateStatuses(Integer id,TaskStatus task) {
        TaskStatus existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new RuntimeException("Status name is required");
        }
        if (task.getName().trim().length() > 50) {
            throw new RuntimeException("Status name cannot exceed 50 characters");
        }
        if (task.getDescription().trim().isEmpty()) { // NOT EMPTY
            throw new RuntimeException("Status description is not empty");
        }
        if (task.getDescription().trim().length() > 200) {
            throw new RuntimeException("Status description cannot exceed 200 characters");
        }
        try {
            Integer oldStatusId = existingTask.getId();
            if (task.getId() != null) {
                taskRepository.updateStatusId(oldStatusId, task.getId());
            }
            task.setId(id);
            return repository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task status to the database", e);
        }
    }

    // DELETE
    // DELETE
    @Transactional
    public TaskStatus removeStatuses(Integer id) {
        TaskStatus status = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
        if ("NO_STATUS".equals(status.getName())) {
            throw new RuntimeException("Cannot delete 'No Status'.");
        }
        try {
            repository.delete(status);
            return status;
        } catch (Exception e) {
            throw new ItemNotFoundException("Failed to delete status: " + e.getMessage());
        }
    }

    @Transactional
    public TaskStatus transferStatuses(Integer id, Integer newId) {

        TaskStatus existingStatus = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Status not found"));

        TaskStatus newStatus = repository.findById(newId).orElseThrow(
                () -> new ItemNotFoundException("New status not found"));

        if (existingStatus == null || newStatus == null) {
            throw new ItemNotFoundException("Status not found");
        }

        List<Task> tasks = taskRepository.findByStatusTasksId(id);
        for (Task task : tasks) {
            task.setStatusTasks(newStatus);
        }
        // บันทึกการเปลี่ยนแปลงสถานะของงาน
        taskRepository.saveAll(tasks);

        return newStatus;
    }
}
