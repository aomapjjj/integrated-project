package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.dtos.TaskNewDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.repositories.TaskRepository;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    //GET ALL TASKS
    public List<TaskDTO> getAllTasks(){
        return listMapper.mapList(repository.findAll(), TaskDTO.class, modelMapper);
    }
    //GET ALL BY ID
    public Task findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id "+ id + " does not exist !!!"));
    }
    // ADD
    @Transactional
    public Task createNewTasks(TaskNewDTO task){
        Task task1 = modelMapper.map(task, Task.class);
        TaskStatus status;
        try {
             status = statusRepository.findById(Integer.parseInt(task.getStatus())).orElseThrow(
                     () -> new InternalServerErrorException("Invalid status ID"));

        } catch (Exception exception) {
             status = statusRepository.findByName(task.getStatus());
        }
        task1.setStatusTasks(status);

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
        try {
            Task savedTask = repository.save(task1);
            return savedTask;
        } catch (Exception exception) {
            throw new ItemNotFoundException("Failed to save task");
        }
    }
    // DELETE
    @Transactional
    public TaskDTO removeTasks(Integer id){
        Task task = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));
        TaskDTO deletedTaskDTO = modelMapper.map(task, TaskDTO.class);
        repository.delete(task);
        return deletedTaskDTO;
    }
    // EDIT
    @Transactional
    public Task updateTask(Integer id, TaskNewDTO task) {
        Task existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        Task task1 = modelMapper.map(task, Task.class);
        TaskStatus status = statusRepository.findByName(task.getStatus());
        task1.setStatusTasks(status);

        if (task.getTitle() != null && !task.getTitle().isEmpty()) {
            task.setTitle(task.getTitle().trim());
        }
        if (task.getDescription() != null) {
            task.setDescription(task.getDescription().trim());
        }
        if (task.getAssignees() != null) {
            task.setAssignees(task.getAssignees().trim());
        }
        if (task.getStatus() != null) {
            task.setStatus(task.getStatus());
        }
        task1.setId(id);
        return repository.save(task1);
    }
}
