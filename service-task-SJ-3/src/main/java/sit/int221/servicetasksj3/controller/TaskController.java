package sit.int221.servicetasksj3.controller;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.services.TaskService;

import java.util.List;


@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:5173/")
//@CrossOrigin(origins = "http://ip23sj3.sit.kmutt.ac.th")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public List<TaskDTO> getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id){
        return service.findByID(id);
    }

    @PostMapping("")
    public ResponseEntity<Task> createNewTasks(@Valid @RequestBody Task task){
        Task createdTask = service.createNewTasks(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
}
