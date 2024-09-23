package sit.int221.servicetasksj3.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.boardsDTO.*;
import sit.int221.servicetasksj3.dtos.limitsDTO.SimpleLimitDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.*;
import sit.int221.servicetasksj3.dtos.tasksDTO.*;
import sit.int221.servicetasksj3.entities.*;
import sit.int221.servicetasksj3.services.*;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
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

    // false - read operation (get)
    // true - write operation (create, update, delete)

    // Board
    @GetMapping("")
    public ResponseEntity<List<BoardResponseDTO>> getBoardIdByOwner() {
        List<BoardResponseDTO> boardIds = boardService.getBoardIdByOwner();
        return ResponseEntity.ok(boardIds);
    }
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable String boardId) {
        boardService.checkAuthorization(boardId, false);
        BoardResponseDTO boardResponse = boardService.getBoardById(boardId);
        return ResponseEntity.ok(boardResponse);
    }
    @PostMapping("")
    public ResponseEntity<BoardResponseDTO> createNewBoards(@Valid @RequestBody BoardRequestDTO boardRequest) {
        BoardResponseDTO boardResponse = boardService.createNewBoard(boardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponse);
    }
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Board> removeBoard(@PathVariable String boardId) {
        boardService.checkAuthorization(boardId, true);
        Board deleted = boardService.removeBoard(boardId);
        return ResponseEntity.ok().body(deleted);
    }
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> editBoard(@PathVariable String boardId, @Valid @RequestBody BoardRequestDTO boardRequest) {
        boardService.checkAuthorization(boardId, true);
        BoardResponseDTO updatedBoard = boardService.editBoard(boardId, boardRequest);
        return ResponseEntity.ok(updatedBoard);
    }
    @PatchMapping("/{boardId}")
    public ResponseEntity<VisibilityDTO> editBoardVisibility(@PathVariable String boardId, @Valid @RequestBody VisibilityDTO visibility) {
        boardService.checkAuthorization(boardId, true);
        VisibilityDTO updatedVisibility = boardService.editBoardVisibility(boardId, visibility);
        return ResponseEntity.ok(updatedVisibility);
    }

    // Task of board
    @GetMapping("/{boardId}/tasks")
    public List<TaskNewDTO> getAllTasksFiltered(
            @PathVariable String boardId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String[] filterStatuses
    ) {
//        boardService.validateBoardExists(boardId);
        boardService.checkAuthorization(boardId, false);
        return taskService.getAllTasksFiltered(boardId, sortBy, filterStatuses);
    }
    @GetMapping("/{boardId}/tasks/{taskId}")
    public SimpleTaskDTO getTaskById(@PathVariable String boardId,@PathVariable Integer taskId) {
        boardService.checkAuthorization(boardId, false);
        Task task = taskService.findByID(boardId, taskId);
        SimpleTaskDTO simpleTaskDTO = modelMapper.map(task, SimpleTaskDTO.class);
        return simpleTaskDTO;
    }
    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<TaskDTOTwo> createNewTasks(@PathVariable String boardId, @Valid @RequestBody TaskNewDTO task) {
        boardService.checkAuthorization(boardId, true);
        Task createTask = taskService.createNewTasks(boardId, task);
        TaskDTOTwo createdTaskDTO = modelMapper.map(createTask, TaskDTOTwo.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
    }
    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> removeTasks(@PathVariable String boardId,@PathVariable Integer taskId) {
        boardService.checkAuthorization(boardId, true);
        TaskDTO deletedTaskDTO = taskService.removeTasks(boardId, taskId);
        return ResponseEntity.ok().body(deletedTaskDTO);
    }
    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTOTwo> updateTasks(@PathVariable String boardId, @PathVariable Integer taskId, @Valid @RequestBody TaskNewDTO task) {
        boardService.checkAuthorization(boardId, true);
        Task updatedTask = taskService.updateTask(boardId, taskId, task);
        TaskDTOTwo updatedTaskDTO = modelMapper.map(updatedTask, TaskDTOTwo.class);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    // Statuses of board
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<List<StatusDTOTwo>> getAllStatuses(@PathVariable String boardId) {
//        boardService.validateBoardExists(boardId);
        boardService.checkAuthorization(boardId, false);
        return ResponseEntity.ok(statusService.getAllStatuses(boardId));
    }
    @GetMapping("/{boardId}/statuses/{statusId}")
    public TaskStatus getStatusesById(@PathVariable String boardId, @PathVariable Integer statusId){
        boardService.checkAuthorization(boardId, false);
        return statusService.getStatusesById(boardId, statusId);
    }
    @GetMapping("/{boardId}/statuses/limit")
    public ResponseEntity<TaskLimit> getStatusesLimit(@PathVariable String boardId){
        boardService.checkAuthorization(boardId, false);
        return ResponseEntity.ok(statusService.getStatusesLimit(boardId));
    }
    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<StatusDTO> createNewStatuses(@PathVariable String boardId, @Valid @RequestBody StatusDTO status) {
        boardService.checkAuthorization(boardId, true);
        StatusDTO createdStatus = statusService.createNewStatuses(boardId, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }
    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatuses(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody TaskStatus task) {
        boardService.checkAuthorization(boardId, true);
        return ResponseEntity.ok(statusService.updateStatuses(boardId, statusId, task));
    }
    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> removeStatuses(@PathVariable String boardId,@Valid @PathVariable Integer statusId){
        boardService.checkAuthorization(boardId, true);
        return ResponseEntity.ok(statusService.removeStatuses(boardId, statusId));
    }
    @DeleteMapping("/{boardId}/statuses/{statusId}/{newId}")
    public ResponseEntity<Object> removeStatusAndReplace(@PathVariable String boardId, @Valid @PathVariable Integer statusId , @PathVariable Integer newId){
        boardService.checkAuthorization(boardId, true);
        return ResponseEntity.ok(statusService.transferStatuses(boardId,statusId,newId));
    }
    @PatchMapping("/{boardId}/statuses/maximumtask")
    public ResponseEntity<SimpleLimitDTO> updateLimitTask(
            @PathVariable String boardId,
            @RequestParam @Min(0) @Max(30) Integer maximumTask,
            @RequestParam Boolean isLimit) {
        boardService.checkAuthorization(boardId, true);
        return ResponseEntity.ok(statusService.updateLimitTask(boardId, maximumTask, isLimit));
    }
}