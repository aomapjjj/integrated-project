package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.StatusDTO;
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
        return repository.findById(id).orElseThrow(() -> new ItemNotFoundException("NOT FOUND"));
    }

    @Transactional
    public List<StatusDTO> createNewTasks(StatusDTO statusDTO) {
        if (statusDTO == null) {
            throw new RuntimeException("Status data is null");
        }
        if (statusDTO.getName() == null || statusDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Status name is required");
        }
        if (statusDTO.getName().trim().length() > 50) {
            throw new RuntimeException("Status name cannot exceed 50 characters");
        }
        if (statusDTO.getDescription() != null && statusDTO.getDescription().trim().length() > 200) {
            throw new RuntimeException("Status description cannot exceed 200 characters");
        }

        // ตรวจสอบว่าสถานะที่กำลังสร้างนั้นมีชื่อซ้ำกับสถานะที่มีอยู่แล้วหรือไม่
        List<TaskStatus> existingStatuses = repository.findByName(statusDTO.getName());
        if (!existingStatuses.isEmpty()) {
            throw new RuntimeException("Status with name '" + statusDTO.getName() + "' already exists");
        }

        try {
            TaskStatus status = new TaskStatus();
            status.setName(statusDTO.getName());
            status.setDescription(statusDTO.getDescription());

            TaskStatus savedStatus = repository.save(status);
            return Collections.singletonList(modelMapper.map(savedStatus, StatusDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task");
        }
    }


}



//    public StatusDTO createNewTasks(StatusDTO taskDTO) {
//        Task task = modelMapper.map(taskDTO, Task.class);
//        List<TaskStatus> status = repository.findByStatusName(taskDTO.getStatusName());
//
//        if (status == null) {
//            status = new TaskStatus();
//            status.setName(taskDTO.getName());
//            status.setDescription(taskDTO.getDescription());
//            status = repository.save(status);
//        }
//
//        task.setStatusTasks(status);
//
//        task = repository.save(task);
//
//        return modelMapper.map(task, StatusDTO.class);
//    }
//    public StatusDTO createNewTasks(StatusDTO taskDTO){
//        Task task = modelMapper.map(taskDTO, Task.class);
//        TaskStatus status = repository.findByStatusName(taskDTO.getStatus());
//
//        task.setStatus(status);
//        status.
//    }

//    public StatusEntity createNewStatuses(StatusEntity status){
//        if (status.getName() == null || status.getName().isEmpty()){
//            throw new RuntimeException();
//        }
//        try {
//            return repository.save(status);
//        } catch (Exception exception) {
//            throw new ItemNotFoundException("Failed to save task");
//        }
//    }
    // DELETE STATUSES

    // EDIT STATUSES

