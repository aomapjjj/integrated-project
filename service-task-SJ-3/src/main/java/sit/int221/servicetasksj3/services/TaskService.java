package sit.int221.servicetasksj3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.entities.Task;
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
    public List<TaskDTO> getAllTasks(){
        return listMapper.mapList(repository.findAll(), TaskDTO.class, modelMapper);
    }
    public Task findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task id "+ id + " does not exist !!!"));
    }
    // ADD
    @Transactional
    public Task createNewTasks(Task task) {
        return repository.save(task);
    }

    // DELETE
    @Transactional
    public List<TaskDTO> removeTasks(Integer id){
        Task task = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);

        List<Task> remainingTasks = repository.findAll();
        return listMapper.mapList(remainingTasks, TaskDTO.class, modelMapper);
    }

    // EDIT
    @Transactional
    public Task updateTakes(Integer id, Task task) {
        // ค้นหา Task จาก repository ด้วย id
        Task existingTask = repository.findById(id).orElseThrow(
<<<<<<< Updated upstream
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        // เช็คว่า id ของ Task ที่พบเหมือนกับ id
        if (!existingTask.getId().equals(task.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISMATCH");
        }
        return repository.save(task);
=======
                () -> new ItemNotFoundException("NOT FOUND"));
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new ItemNotFoundException("Title is required");
        } else {
            task.setId(id);
            return repository.save(task);
        }
>>>>>>> Stashed changes
    }
}


