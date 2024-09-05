package sit.int221.servicetasksj3.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardRequestDTO;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.dtos.limitsDTO.SimpleLimitDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTOTwo;
import sit.int221.servicetasksj3.dtos.tasksDTO.SimpleTaskDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskDTO;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskDTOTwo;
import sit.int221.servicetasksj3.dtos.tasksDTO.TaskNewDTO;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskLimit;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.services.BoardService;
import sit.int221.servicetasksj3.services.StatusService;
import sit.int221.servicetasksj3.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/boards")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("")
    public ResponseEntity<List<BoardResponseDTO>> getBoardIdByOwner() {
        List<BoardResponseDTO> boardIds = boardService.getBoardIdByOwner();
        return ResponseEntity.ok(boardIds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable String id) {
        BoardResponseDTO boardResponse = boardService.getBoardById(id);
        return ResponseEntity.ok(boardResponse);
    }

    @PostMapping("")
    public ResponseEntity<BoardResponseDTO> createNewBoards(@Valid @RequestBody BoardRequestDTO boardRequest) {
        BoardResponseDTO boardResponse = boardService.createNewBoard(boardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponse);
    }

    // Task of board
    @GetMapping("/{boardId}/tasks")
    public List<TaskNewDTO> getAllTasksFiltered(
            @PathVariable String boardId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String[] filterStatuses
    ) {
        return taskService.getAllTasksFiltered(boardId, sortBy, filterStatuses);
    }
    @GetMapping("/{boardId}/tasks/{taskId}")
    public SimpleTaskDTO getTaskById(@PathVariable String boardId,@PathVariable Integer taskId) {
        Task task = taskService.findByID(boardId, taskId);
        SimpleTaskDTO simpleTaskDTO = modelMapper.map(task, SimpleTaskDTO.class);
        return simpleTaskDTO;
    }
    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<TaskDTOTwo> createNewTasks(@PathVariable String boardId, @Valid @RequestBody TaskNewDTO task) {
        Task createTask = taskService.createNewTasks(boardId, task);
        TaskDTOTwo createdTaskDTO = modelMapper.map(createTask, TaskDTOTwo.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
    }
    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> removeTasks(@PathVariable String boardId,@PathVariable Integer taskId) {
        TaskDTO deletedTaskDTO = taskService.removeTasks(boardId, taskId);
        return ResponseEntity.ok().body(deletedTaskDTO);
    }
    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTOTwo> updateTasks(@PathVariable String boardId, @PathVariable Integer taskId, @Valid @RequestBody TaskNewDTO task) {
        Task updatedTask = taskService.updateTask(boardId, taskId, task);
        TaskDTOTwo updatedTaskDTO = modelMapper.map(updatedTask, TaskDTOTwo.class);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    // Statuses of board
    @GetMapping("/{boardId}/statuses")
    public List<StatusDTOTwo> getAllStatuses(@PathVariable String boardId) {
        return statusService.getAllStatuses(boardId);
    }
    @GetMapping("/{boardId}/statuses/{statusId}")
    public TaskStatus getStatusesById(@PathVariable String boardId, @PathVariable Integer statusId){
        return statusService.getStatusesById(boardId, statusId);
    }
    @GetMapping("/{boardId}/statuses/limit")
    public ResponseEntity<TaskLimit> getStatusesLimit(@PathVariable String boardId){
        return ResponseEntity.ok(statusService.getStatusesLimit(boardId));
    }
    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<StatusDTO> createNewStatuses(@PathVariable String boardId, @Valid @RequestBody StatusDTO status) {
        StatusDTO createdStatus = statusService.createNewStatuses(boardId, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }
    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatuses(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody TaskStatus task) {
        return ResponseEntity.ok(statusService.updateStatuses(boardId, statusId, task));
    }
    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> removeStatuses(@PathVariable String boardId,@Valid @PathVariable Integer statusId){
        return ResponseEntity.ok(statusService.removeStatuses(boardId, statusId));
    }
    @DeleteMapping("/{boardId}/statuses/{statusId}/{newId}")
    public ResponseEntity<Object> removeStatusAndReplace(@PathVariable String boardId, @Valid @PathVariable Integer statusId , @PathVariable Integer newId){
        return ResponseEntity.ok(statusService.transferStatuses(boardId,statusId,newId));
    }
    @PatchMapping("/{boardId}/statuses/maximumtask")
    public ResponseEntity<SimpleLimitDTO> updateLimitTask(
            @PathVariable String boardId,
            @RequestParam @Min(0) @Max(30) Integer maximumTask,
            @RequestParam Boolean isLimit) {
        return ResponseEntity.ok(statusService.updateLimitTask(boardId, maximumTask, isLimit));
    }
}