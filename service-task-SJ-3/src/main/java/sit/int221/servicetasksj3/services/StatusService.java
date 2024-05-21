package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.StatusDTO;
import sit.int221.servicetasksj3.dtos.StatusDTOTwo;
import sit.int221.servicetasksj3.dtos.StatusLimitTaskDTO;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;

import java.util.List;
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

    //GET
    public List<StatusDTOTwo> getAllStatuses() {
        return listMapper.mapList(repository.findAll(), StatusDTOTwo.class, modelMapper);
    }

    public TaskStatus getStatusesById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // ADD
    @Transactional
    public StatusDTO createNewStatuses(StatusDTO statusDTO) {
        // Trim leading and trailing whitespace
        statusDTO.setName(statusDTO.getName().trim());
        statusDTO.setDescription(statusDTO.getDescription() != null ? statusDTO.getDescription().trim() : null);

        if (statusDTO.getName() == null || statusDTO.getName().isEmpty()) {
            throw new InternalServerErrorException("Status name is required");
        }
        if (statusDTO.getName().length() > 50) {
            throw new InternalServerErrorException("Status name cannot exceed 50 characters");
        }
        if (statusDTO.getDescription() != null && statusDTO.getDescription().length() > 200) {
            throw new InternalServerErrorException("Status description cannot exceed 200 characters");
        }
        // Check if status with the same name already exists
        TaskStatus existingStatus = repository.findByName(statusDTO.getName());
        if (existingStatus != null) {
            throw new InternalServerErrorException("Status with name '" + statusDTO.getName() + "' already exists");
        }
        try {
            TaskStatus savedStatus = repository.save(modelMapper.map(statusDTO, TaskStatus.class));
            return modelMapper.map(savedStatus, StatusDTO.class);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save task status to the database");
        }
    }


    // EDIT
    @Transactional
    public TaskStatus updateStatuses(Integer id, TaskStatus task) {
        TaskStatus existingTask = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("NOT FOUND"));
        // Check if the status ID is for "No Status"
        if (existingTask.getId() == 1) {
            throw new InternalServerErrorException("The status 'No Status' cannot be changed");
        }
        // Check if the task name is "No Status" and not changed
        if ("No Status".equalsIgnoreCase(existingTask.getName()) && !task.getName().equalsIgnoreCase("No Status")) {
            throw new InternalServerErrorException("The status name 'No Status' should not be changed");
        }
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new InternalServerErrorException("Status name is required");
        }
        if (task.getName().trim().length() > 50) {
            throw new InternalServerErrorException("Name must be between 0 and 50");
        }
        if (task.getDescription() != null && task.getDescription().trim().length() > 200) {
            throw new InternalServerErrorException("Description must be between 0 and 200");
        }
        // Check if the status is unique
        TaskStatus existingStatus = repository.findByName(task.getName().trim());
        if (existingStatus != null && !existingStatus.getId().equals(id)) {
            throw new ValidationException("Status name must be unique");
        }
        try {
            Integer oldStatusId = existingTask.getId();
            if (task.getId() != null) {
                taskRepository.updateStatusId(oldStatusId, task.getId());
            }
            task.setId(id);
            return repository.save(task);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save task status to the database");
        }
    }

    // DELETE
    @Transactional
    public TaskStatus removeStatuses(Integer id) {
        TaskStatus status = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
        if ("No Status".equals(status.getName())) {
            throw new InternalServerErrorException("Cannot delete 'No Status'");
        }
        try {
            repository.delete(status);
            return status;
        } catch (Exception e) {
            throw new ItemNotFoundException("Failed to delete status to the database" + e.getMessage());
        }
    }

    // TRANSFER
    @Transactional
    public TaskStatus transferStatuses(Integer id, Integer newId) {
        //เช็คว่า oldId และ newId ไม่เท่ากัน
        if (id.equals(newId)) {
            throw new InternalServerErrorException("Cannot use old Status Id as New Status Id");
        }
        if (!repository.existsById(id)) {
            throw new ItemNotFoundException("Status not found with id " + id);
        }
        if (!repository.existsById(newId)) {
            throw new ItemNotFoundException("Status not found with id " + newId);
        }
        TaskStatus existingStatus  = repository.findById(id).orElseThrow();
        Integer newStatus = repository.findById(newId).orElseThrow().getId();
        try {
            taskRepository.updateStatusId(existingStatus .getId() , newStatus);
            repository.delete(existingStatus);
            return existingStatus;
        }catch (Exception message){
            throw new ItemNotFoundException("NOT FOUND");
        }
    }
    // LIMIT
//    @Transactional
//    public StatusLimitTaskDTO updateStatusLimit(Integer id, StatusLimitTaskDTO statusLimitTaskDTO){
//        TaskStatus existingStatus = repository.findById(id).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND"));
//
//    }
}