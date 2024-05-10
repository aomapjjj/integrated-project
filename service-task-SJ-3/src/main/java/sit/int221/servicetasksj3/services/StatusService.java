package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.StatusDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.StatusRepository;

import java.util.Collections;
import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<StatusDTO> getAllTasks() {
        return listMapper.mapList(repository.findAll(), StatusDTO.class, modelMapper);
    }

    public TaskStatus findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    @Transactional
    public List<StatusDTO> createNewTasks(StatusDTO statusDTO) {
        if (statusDTO.getName() == null || statusDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Status name is required");
        }
        if (statusDTO.getName().trim().length() > 50) {
            throw new RuntimeException("Status name cannot exceed 50 characters");
        }
        if (statusDTO.getDescription().trim().length() > 200) {
            throw new RuntimeException("Status description cannot exceed 200 characters");
        }

        // ตรวจสอบว่าสถานะที่กำลังสร้างนั้นมีชื่อซ้ำกับสถานะที่มีอยู่แล้วหรือไม่
        List<TaskStatus> existingStatuses = repository.findByName(statusDTO.getName());
        if (!existingStatuses.isEmpty()) {
            throw new RuntimeException("Status with name '" + statusDTO.getName() + "' already exists");
        }
        try {
            // Create TaskStatus entity
            TaskStatus status = new TaskStatus();
                status.setName(statusDTO.getName());
                status.setDescription(statusDTO.getDescription());
            // Save TaskStatus entity
            TaskStatus savedStatus = repository.save(status);
            // Map the saved TaskStatus entity to StatusDTO
            return Collections.singletonList(modelMapper.map(savedStatus, StatusDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task status to the database", e);
        }
    }

//    @Transactional
//    public TaskStatus updateTasks(Integer id, TaskStatus task) {
//        TaskStatus existingTask = repository.findById(id).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND"));
//
//        if (task.getName() == null || task.getName().trim().isEmpty()) {
//            throw new RuntimeException("Status name is required");
//        }
//        if (task.getName().trim().length() > 50) {
//            throw new RuntimeException("Status name cannot exceed 50 characters");
//        }
//        if (task.getDescription().trim().length() > 200) {
//            throw new RuntimeException("Status description cannot exceed 200 characters");
//        }
//        try {
//            Integer oldStatusId = existingTask.getId();
//            if (!task.getId().equals(oldStatusId.toString())) {
//                repository.updateStatusId(oldStatusId, task.getId());
//            }
//            task.setId(id);
//            return repository.save(task);
//        } catch (Exception e) {
//            throw new ItemNotFoundException("Database Exception");
//        }
//    }



}
