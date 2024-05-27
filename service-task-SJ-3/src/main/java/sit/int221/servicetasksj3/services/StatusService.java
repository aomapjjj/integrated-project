package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.limitsDTO.SimpleLimitDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTOTwo;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskLimit;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.LimitRepository;
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
    private LimitRepository limitRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    // GET ALL STATUSES
    public List<StatusDTOTwo> getAllStatuses() {
        return listMapper.mapList(repository.findAll(), StatusDTOTwo.class, modelMapper);
    }

    // GET STATUS BY ID
    public TaskStatus getStatusesById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // GET STATUS LIMIT
    public TaskLimit getStatusesLimit() {
        return limitRepository.findById(1).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // ADD NEW STATUS
    @Transactional
    public StatusDTO createNewStatuses(StatusDTO statusDTO) {
        // Trim leading and trailing whitespace
        if (statusDTO.getName() != null) {
            statusDTO.setName(statusDTO.getName().trim());
        }
        if (statusDTO.getDescription() != null) {
            statusDTO.setDescription(statusDTO.getDescription().trim());
        }

        // Validation
        ValidationException validationError = new ValidationException("Validation error");
        if (statusDTO.getName() == null || statusDTO.getName().isEmpty()) {
            validationError.addValidationError("name", "must not be null");
        } else {
            if (statusDTO.getName().length() > 50) {
                validationError.addValidationError("name", "size must be between 0 and 50");
            }
        }
        if (statusDTO.getDescription() != null && statusDTO.getDescription().length() > 200) {
            validationError.addValidationError("description", "size must be between 0 and 200");
        }
        if (!validationError.getErrors().isEmpty()) {
            throw validationError;
        }

        TaskStatus existingStatus = repository.findByName(statusDTO.getName());
        if (existingStatus != null) {
            validationError.addValidationError("name", "must be unique");
            throw validationError;
        }

        // Check if task limit is enabled and the status has reached the limit
        TaskLimit taskLimit = limitRepository.findById(1).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            long statusCount = repository.count();
            if (statusCount >= taskLimit.getMaximumTask()) {
                validationError.addValidationError("status", "the status has reached the limit");
                throw validationError;
            }
        }

        try {
            TaskStatus savedStatus = repository.save(modelMapper.map(statusDTO, TaskStatus.class));
            return modelMapper.map(savedStatus, StatusDTO.class);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save task status to the database: " + e.getMessage());
        }
    }

    // UPDATE STATUS
    @Transactional
    public TaskStatus updateStatuses(Integer id, TaskStatus task) {
        TaskStatus existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        // Validation
        ValidationException validationError = new ValidationException("Validation error");
        if (existingTask.getName() == null) {
            validationError.addValidationError("name", "must not be null");
        } else {
            if ("No Status".equalsIgnoreCase(existingTask.getName()) && (task.getName() == null || !task.getName().equalsIgnoreCase("No Status"))) {
                validationError.addValidationError("no status", "cannot be");
            }
            if ("Done".equalsIgnoreCase(existingTask.getName()) && (task.getName() == null || !task.getName().equalsIgnoreCase("Done"))) {
                validationError.addValidationError("done", "cannot be");
            }
        }
        if (task.getName() == null || task.getName().isEmpty()) {
            validationError.addValidationError("name", "must not be null");
        } else {
            if (task.getName().length() > 50) {
                validationError.addValidationError("name", "size must be between 0 and 50");
            }
        }
        if (task.getDescription() != null && task.getDescription().length() > 200) {
            validationError.addValidationError("description", "size must be between 0 and 200");
        }

        if (!validationError.getErrors().isEmpty()) {
            throw validationError;
        }

        // Check if task limit is enabled and the status has reached the limit
        TaskLimit taskLimit = limitRepository.findById(1).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            List<Task> tasks = taskRepository.findByStatus(existingTask);
            if (tasks.size() >= taskLimit.getMaximumTask()) {
                validationError.addValidationError("status", "the status has reached the limit");
                throw validationError;
            }
        }

        // Check if the status is unique
        TaskStatus existingStatus = repository.findByName(task.getName().trim());
        if (existingStatus != null && !existingStatus.getId().equals(id)) {
            validationError.addValidationError("name", "must be unique");
            throw validationError;
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

    // DELETE STATUS
    @Transactional
    public TaskStatus removeStatuses(Integer id) {
        TaskStatus status = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        ValidationException validationError = new ValidationException("Validation error");
        if ("No Status".equalsIgnoreCase(status.getName())) {
            validationError.addValidationError("no status", "cannot be");
            throw validationError;
        }
        if ("Done".equalsIgnoreCase(status.getName())) {
            validationError.addValidationError("done", "cannot be");
            throw validationError;
        }

        if ("No Status".equals(status.getName())) {
            throw new ValidationException("Cannot delete 'No Status'");
        }
        if ("Done".equals(status.getName())) {
            throw new ValidationException("Cannot delete 'Done'");
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
        // Check if the new status ID exists and throw a ValidationException if not
        ValidationException validationError = new ValidationException("Validation error");
        if (id.equals(newId)) {
            validationError.addValidationError("message", "destination status for task transfer must be different from current status");
            throw validationError;
        }
        // Check that oldId and newId are not the same
        if (!repository.existsById(newId)) {
            validationError.addValidationError("message", "The specified status for task transfer does not exist");
            throw validationError;
        }


        TaskStatus oldStatus = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Old status not found"));
        TaskStatus newStatus = repository.findById(newId).orElseThrow(() -> new ItemNotFoundException("New status not found"));

        // Check the task limit
        TaskLimit taskLimit = limitRepository.findById(1).orElseThrow(() -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            List<Task> newStatusTasks = taskRepository.findByStatus(newStatus);
            List<Task> oldStatusTasks = taskRepository.findByStatus(oldStatus);
            if (newStatusTasks.size() + oldStatusTasks.size() > taskLimit.getMaximumTask()) {
                validationError.addValidationError("status", "the destination status cannot be over the limit after transfer");
                throw validationError;
            }
        }

        TaskStatus existingStatus = repository.findById(id).orElseThrow();
        Integer newStatuses = repository.findById(newId).orElseThrow().getId();

        try {
            taskRepository.updateStatusId(existingStatus.getId(), newStatuses);
            repository.delete(existingStatus);
            return existingStatus;
        } catch (Exception message) {
            throw new ItemNotFoundException("NOT FOUND");
        }
    }

    // SET STATUS LIMIT
    @Transactional
    public SimpleLimitDTO updateLimitTask(Integer maximumTask, Boolean isLimit) {
        // Update the task limit configuration
        limitRepository.updateLimit(1, maximumTask, isLimit);
        return new SimpleLimitDTO(maximumTask, isLimit);
    }
}