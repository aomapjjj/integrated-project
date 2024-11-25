package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskNewDTO;
import sit.int221.servicetasksj3.entities.*;
import sit.int221.servicetasksj3.exceptions.*;
import sit.int221.servicetasksj3.repositories.*;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    MicrosoftDetailRepository microsoftDetailRepository;



    //GET ALL TASKS V.2
    @Transactional
    public List<TaskNewDTO> getAllTasksFilteredV2(String sortBy, String[] filterStatuses) {
        Sort sort = Sort.by(Sort.Order.asc(sortBy != null ? sortBy : "id"));
        try {
            List<Task> tasks;
            if (filterStatuses != null && filterStatuses.length > 0) {
                tasks = repository.findTasksByStatusOnly(filterStatuses, sort);
            } else {
                tasks = repository.findAll(sort);
            }
            if ("status".equals(sortBy)) {
                tasks.sort(Comparator.comparing(task -> task.getStatus().getName()));
            }
            return tasks.stream()
                    .map(task -> {
                        TaskNewDTO taskNewDTO = modelMapper.map(task, TaskNewDTO.class);
                        taskNewDTO.setStatus(task.getStatus().getName());
                        return taskNewDTO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new InternalServerErrorException("Failed to sortBy: " + exception.getMessage());
        }
    }

    //GET ALL TASKS
    @Transactional
    public List<TaskNewDTO> getAllTasksFiltered(String boardId, String sortBy, String[] filterStatuses) {
        Sort sort = Sort.by(Sort.Order.asc(sortBy != null ? sortBy : "id"));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        String oid = userRepository.findById(board.getOwnerId())
                .map(Users::getOid) // หากเจอใน userRepository ให้ดึง oid
                .orElseGet(() ->
                        microsoftDetailRepository.findById(board.getOwnerId())
                                .map(MicrosoftDetail::getOid) // หากเจอใน microsoftDetailRepository ให้ดึง oid
                                .orElseThrow(() -> new ItemNotFoundException(
                                        "Owner not found with ID: " + board.getOwnerId()
                                ))
                );

        try {
            List<Task> tasks;
            if (filterStatuses != null && filterStatuses.length > 0) {
                tasks = repository.findTasksByStatus(boardId, oid, filterStatuses, sort);
            } else {
                tasks = repository.findAllByBoardIdAndOid(boardId, oid, sort);
            }
            if ("status".equals(sortBy)) {
                tasks.sort(Comparator.comparing(task -> task.getStatus().getName()));
            }
            return tasks.stream()
                    .map(task -> {
                        TaskNewDTO taskNewDTO = modelMapper.map(task, TaskNewDTO.class);
                        taskNewDTO.setStatus(task.getStatus().getName());
                        return taskNewDTO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new InternalServerErrorException("Failed to sortBy: " + exception.getMessage());
        }
    }

    //GET ALL BY ID
    public Task findByID(String boardId, Integer id) {
        return repository.findByBoard_IdAndId(boardId, id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " does not exist !!!"));
    }

    // ADD
    @Transactional
    public Task createNewTasks(String boardId, TaskNewDTO task) {
        Task task1 = modelMapper.map(task, Task.class);
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with ID: " + boardId));
        task1.setBoard(board);
        TaskStatus status;

        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isWriteAccess = collaboratorService.hasWriteAccess(boardId, currentUser.getOid());

        if (!isWriteAccess && !board.getOwnerId().equals(currentUser.getOid())) {
            throw new UnauthorizedException("You are not the owner of this board");
        }

        try {
            status = statusRepository.findById(Integer.parseInt(task.getStatus())).orElseThrow(
                    () -> new InternalServerErrorException("Invalid status ID"));
        } catch (Exception exception) {
            status = statusRepository.findByNameAndBoardId(task.getStatus(), boardId);
        }
        task1.setStatus(status);

        // Validation
        ValidationException validationError = new ValidationException("Validation error");
        if (task.getTitle() == null) {
            validationError.addValidationError("title", "must not be null");
        }
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            validationError.addValidationError("title", "must not be blank");
        }
        if (task.getTitle() != null && task.getTitle().length() > 100) {
            validationError.addValidationError("title", "size must be between 0 and 100");
        }
        if (task.getDescription() != null && task.getDescription().length() > 500) {
            validationError.addValidationError("description", "size must be between 0 and 500");
        }
        if (task.getAssignees() != null && task.getAssignees().length() > 30) {
            validationError.addValidationError("assignees", "size must be between 0 and 30");
        }
        if (status == null) {
            validationError.addValidationError("status", "does not exist");
        }
        if (!validationError.getErrors().isEmpty()) {
            throw validationError;
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new InternalServerErrorException("NOT FOUND");
        }
        if (task.getTitle().trim().length() > 100) {
            throw new InternalServerErrorException("Title cannot exceed 100 characters");
        }
        if (task.getDescription() != null && task.getDescription().trim().length() > 500) {
            throw new InternalServerErrorException("Description cannot exceed 500 characters");
        }
        if (task.getAssignees() != null && task.getAssignees().trim().length() > 30) {
            throw new InternalServerErrorException("Assignees cannot exceed 30 characters");
        }
        if (status == null) {
            throw new ValidationException("status does not exist");
        }

        // Limit
        TaskLimit taskLimit = limitRepository.findByBoardId(boardId).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));
        if (taskLimit.getIsLimit()) {
            if (!status.getName().equals("No Status") && !status.getName().equals("Done")) {
                List<Task> tasks = status.getTasks();
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    status.setTasks(tasks);
                }
                if (tasks.size() >= taskLimit.getMaximumTask()) {
                    validationError.addValidationError("status", "the status has reached the limit");
                }
            }
        }
        try {
            List<Task> taskList = new ArrayList<>();
            taskList.add(task1);
            status.setTasks(taskList);
            statusRepository.save(status);
            Task savedTask = repository.save(task1);
            return savedTask;
        } catch (Exception exception) {
            throw new ItemNotFoundException("Failed to save task");
        }
    }

    // DELETE
    @Transactional
    public TaskDTO removeTasks(String boardId, Integer id) {
        Task task = repository.findByBoard_IdAndId(boardId, id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        if (task.getFiles() != null && !task.getFiles().isEmpty()) {
            task.getFiles().forEach(file -> fileRepository.deleteById(file.getFileId()));
        }

        TaskDTO deletedTaskDTO = modelMapper.map(task, TaskDTO.class);
        repository.delete(task);
        return deletedTaskDTO;
    }

    // EDIT
    @Transactional
    public Task updateTask(String boardId, Integer id, TaskNewDTO task) {
        Task existingTask = repository.findByBoard_IdAndId(boardId, id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new InternalServerErrorException("Invalid board"));


        TaskStatus status = statusRepository.findByNameAndBoardId(task.getStatus(), boardId);

        Task task1 = modelMapper.map(task, Task.class);
        task1.setBoard(board);
        task1.setStatus(status);

        // Validation
        ValidationException validationError = new ValidationException("Validation error");
        if (task.getTitle() == null) {
            validationError.addValidationError("title", "must not be null");
        }
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            validationError.addValidationError("title", "must not be blank");
        }
        if (task.getTitle() != null && task.getTitle().length() > 100) {
            validationError.addValidationError("title", "size must be between 0 and 100");
        }
        if (task.getDescription() != null && task.getDescription().length() > 500) {
            validationError.addValidationError("description", "size must be between 0 and 500");
        }
        if (task.getAssignees() != null && task.getAssignees().length() > 30) {
            validationError.addValidationError("assignees", "size must be between 0 and 30");
        }
        if (status == null) {
            validationError.addValidationError("status", "does not exist");
        }
        if (!validationError.getErrors().isEmpty()) {
            throw validationError;
        }

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new InternalServerErrorException("NOT FOUND");
        }
        if (task.getTitle().trim().length() > 100) {
            throw new InternalServerErrorException("Title cannot exceed 100 characters");
        }
        if (task.getDescription() != null && task.getDescription().trim().length() > 500) {
            throw new InternalServerErrorException("Description cannot exceed 500 characters");
        }
        if (task.getAssignees() != null && task.getAssignees().trim().length() > 30) {
            throw new InternalServerErrorException("Assignees cannot exceed 30 characters");
        }

        // ดึงค่า Limit
        TaskLimit taskLimit = limitRepository.findByBoardId(boardId).orElseThrow(
                () -> new ItemNotFoundException("Task limit configuration not found"));

        if (taskLimit.getIsLimit()) {
            if (!status.getName().equals("No Status") && !status.getName().equals("Done")) {
                List<Task> tasks = status.getTasks();
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    status.setTasks(tasks);
                }
                if (tasks.size() >= taskLimit.getMaximumTask()) {
                    validationError.addValidationError("status", "the status has reached the limit");
                }
            }
        }
        task1.setId(id);
        return repository.save(task1);
    }


}
