package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.TaskRepository;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
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
    public Task createNewTasks(Task task){
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new RuntimeException("NOT FOUND");
        }
        if (task.getTitle().length() > 100) {
            throw new RuntimeException("Title cannot exceed 100 characters");
        }
        if (task.getDescription() != null && task.getDescription().length() > 500) {
            throw new RuntimeException("Description cannot exceed 500 characters");
        }
        if (task.getAssignees() != null && task.getAssignees().length() > 30) {
            throw new RuntimeException("Assignees cannot exceed 30 characters");
        }
        try {
            return repository.save(task);
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
    public Task updateTask(Integer id, Task task) {
        Task existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND"));

        if (task.getTitle() != null && !task.getTitle().isEmpty()) {
            task.setTitle(task.getTitle().trim());
        }
        if (task.getDescription() != null) {
            task.setDescription(task.getDescription().trim());
        }
        if (task.getAssignees() != null) {
            task.setAssignees(task.getAssignees().trim());
        }
        if (task.getStatusTasks() != null) {
            task.setStatusTasks(task.getStatusTasks());
        }
        task.setId(id);
        return repository.save(task);
    }
}
