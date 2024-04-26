package sit.int221.servicetasksj3.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.dtos.TaskDTOTwo;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.services.TaskService;

import java.util.List;


@RestController
@RequestMapping("/v1/tasks")
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
    public ResponseEntity<TaskDTOTwo> getTaskById(@PathVariable Integer id){
        Task task = service.findByID(id);
        TaskDTOTwo taskDTOTwo = modelMapper.map(task, TaskDTOTwo.class);
        return ResponseEntity.ok(taskDTOTwo);
    }
}
