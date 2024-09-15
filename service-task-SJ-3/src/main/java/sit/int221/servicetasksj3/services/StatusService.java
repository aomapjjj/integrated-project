package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.limitsDTO.SimpleLimitDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTOTwo;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskLimit;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.LimitRepository;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class StatusService {
    @Autowired
    private StatusRepository repository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    // GET ALL STATUSES
    public List<StatusDTOTwo> getAllStatuses(String boardId) {
        return listMapper.mapList(repository.findByBoardId(boardId), StatusDTOTwo.class, modelMapper);
    }

    // GET STATUS BY ID
    public TaskStatus getStatusesById(String boardId, Integer id) {
        return repository.findByBoardIdAndId(boardId,id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // GET STATUS LIMIT
    public TaskLimit getStatusesLimit(String boardId) {
        return limitRepository.findByBoardId(boardId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
    }

    // ADD NEW STATUS
    @Transactional
    public StatusDTO createNewStatuses(String boardId, StatusDTO statusDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        // ตรวจสอบว่าผู้ใช้เป็นเจ้าของ board หรือไม่
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!board.getOwnerId().equals(currentUser.getOid())) {
            throw new UnauthorizedException("You are not the owner of this board");
        }

        if (statusDTO.getName() != null) {
            statusDTO.setName(statusDTO.getName().trim());
        }
        if (statusDTO.getDescription() != null) {
            statusDTO.setDescription(statusDTO.getDescription().trim());
        }

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

        // ตรวจสอบว่า status ใน boardId เดียวกันนี้มีชื่อซ้ำหรือไม่
        TaskStatus existingStatus = repository.findByNameAndBoardId(statusDTO.getName(), boardId);
        if (existingStatus != null) {
            validationError.addValidationError("name", "must be unique within the board");
            throw validationError;
        }

        // Check if task limit is enabled and th
        TaskLimit taskLimit = limitRepository.findByBoardId(boardId).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            long statusCount = repository.count();
            if (statusCount > taskLimit.getMaximumTask()) {
                validationError.addValidationError("status", "the status has reached the limit");
                throw validationError;
            }
        }

        try {
            TaskStatus newStatus = modelMapper.map(statusDTO, TaskStatus.class);
            newStatus.setBoardId(boardId);
            TaskStatus savedStatus = repository.save(newStatus);
            return modelMapper.map(savedStatus, StatusDTO.class);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save task status to the database: " + e.getMessage());
        }
    }

    // UPDATE STATUS
    @Transactional
    public TaskStatus updateStatuses(String boardId, Integer id, TaskStatus task) {
        TaskStatus existingTask = repository.findByBoardIdAndId(boardId, id).orElseThrow(
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
        TaskLimit taskLimit = limitRepository.findByBoardId(boardId).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            List<Task> tasks = taskRepository.findByStatus(existingTask);
            if (tasks.size() >= taskLimit.getMaximumTask()) {
                validationError.addValidationError("status", "the status has reached the limit");
                throw validationError;
            }
        }

        // Check if the status is unique
//        TaskStatus existingStatus = repository.findByName(task.getName().trim());
//        if (existingStatus != null && !existingStatus.getId().equals(id)) {
//            validationError.addValidationError("name", "must be unique");
//            throw validationError;
//        }

        // ตรวจสอบสถานะซ้ำใน boardId เดียวกัน
        TaskStatus existingStatus = repository.findByNameAndBoardId(task.getName().trim(), boardId);
        if (existingStatus != null && !existingStatus.getId().equals(id)) {
            validationError.addValidationError("name", "must be unique within the board");
        }

        try {
            Integer oldStatusId = existingTask.getId();
            if (task.getId() != null) {
                taskRepository.updateStatusId(oldStatusId, task.getId());
            }
            task.setId(id);
            task.setBoardId(boardId);
            return repository.save(task);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save task status to the database");
        }
    }

    // DELETE STATUS
    @Transactional
    public TaskStatus removeStatuses(String boardId, Integer id) {
        TaskStatus status = repository.findByBoardIdAndId(boardId,id).orElseThrow(
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
    public TaskStatus transferStatuses(String boardId, Integer id, Integer newId) {
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

        TaskStatus oldStatus = repository.findByBoardIdAndId(boardId, id)
                .orElseThrow(() -> new ItemNotFoundException("Old status not found"));
        TaskStatus newStatus = repository.findByBoardIdAndId(boardId, newId)
                .orElseThrow(() -> new ItemNotFoundException("New status not found"));

        // Check the task limit
        if (!"No Status".equalsIgnoreCase(newStatus.getName()) && !"Done".equalsIgnoreCase(newStatus.getName())) {
            TaskLimit taskLimit = limitRepository.findByBoardId(boardId).orElseThrow(() -> new ItemNotFoundException("Task limit configuration not found"));
            if (taskLimit.getIsLimit()) {
                List<Task> newStatusTasks = taskRepository.findByStatus(newStatus);
                List<Task> oldStatusTasks = taskRepository.findByStatus(oldStatus);
                if (newStatusTasks.size() + oldStatusTasks.size() > taskLimit.getMaximumTask()) {
                    validationError.addValidationError("status", "the destination status cannot be over the limit after transfer");
                    throw validationError;
                }
            }
        }

//        TaskStatus existingStatus = repository.findById(id).orElseThrow();
//        Integer newStatuses = repository.findById(newId).orElseThrow().getId();

        try {
            taskRepository.updateStatusId(oldStatus.getId(), newStatus.getId());
            repository.delete(oldStatus);
            return oldStatus;
        } catch (Exception message) {
            throw new ItemNotFoundException("NOT FOUND");
        }
    }

    // SET STATUS LIMIT
    @Transactional
    public SimpleLimitDTO updateLimitTask(String boardId, Integer maximumTask, Boolean isLimit) {
        // Update the task limit configuration
        limitRepository.updateLimit(boardId, maximumTask, isLimit);
        return new SimpleLimitDTO(maximumTask, isLimit);
    }
}