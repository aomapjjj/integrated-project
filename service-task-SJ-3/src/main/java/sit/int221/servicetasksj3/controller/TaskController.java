package sit.int221.servicetasksj3.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.servicetasksj3.dtos.TaskDTO;
import sit.int221.servicetasksj3.dtos.TaskDTOTwo;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.services.TaskService;

import java.util.List;


@RestController
@RequestMapping("")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/v1/tasks")
    public List<TaskDTO> getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("/v1/tasks/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer id){
        Task task = service.findByID(id);
        TaskDTOTwo taskDTOTwo = modelMapper.map(task, TaskDTOTwo.class);
        return ResponseEntity.ok(taskDTOTwo);
    }
}
