package sit.int221.servicetasksj3.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "https://intproj23.sit.kmutt.ac.th"})

public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CollaboratorService collaboratorService;


    private String getUserId(HttpServletRequest request) {
        String userId = null;
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            userId = jwtTokenUtil.getAllClaimsFromToken(jwtToken.substring(7)).get("oid").toString();
        }
        return userId;
    }

    // Board
    @GetMapping("")
    public ResponseEntity<List<BoardResponseDTO>> getBoardIdByOwner() {
        List<BoardResponseDTO> boardIds = boardService.getBoardIdByOwner();
        return ResponseEntity.ok(boardIds);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable String boardId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        BoardResponseDTO boardResponse = boardService.getBoardById(boardId);
        return ResponseEntity.ok(boardResponse);
    }

    @PostMapping("")
    public ResponseEntity<BoardResponseDTO> createNewBoards(@Valid @RequestBody BoardRequestDTO boardRequest) {
        BoardResponseDTO boardResponse = boardService.createNewBoard(boardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponse);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Board> removeBoard(@PathVariable String boardId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        Board deleted = boardService.removeBoard(boardId);
        return ResponseEntity.ok().body(deleted);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> editBoard(@PathVariable String boardId, @Valid @RequestBody BoardRequestDTO boardRequest, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        BoardResponseDTO updatedBoard = boardService.editBoard(boardId, boardRequest);
        return ResponseEntity.ok(updatedBoard);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<VisibilityDTO> editBoardVisibility(@PathVariable String boardId, @Valid @RequestBody VisibilityDTO visibility, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        VisibilityDTO updatedVisibility = boardService.editBoardVisibility(boardId, visibility);
        return ResponseEntity.ok(updatedVisibility);
    }

    // Task of board
    @GetMapping("/{boardId}/tasks")
    public List<TaskNewDTO> getAllTasksFiltered(
            @PathVariable String boardId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String[] filterStatuses,
            HttpServletRequest request
    ) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return taskService.getAllTasksFiltered(boardId, sortBy, filterStatuses);
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public SimpleTaskDTO getTaskById(@PathVariable String boardId, @PathVariable Integer taskId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        Task task = taskService.findByID(boardId, taskId);
        SimpleTaskDTO simpleTaskDTO = modelMapper.map(task, SimpleTaskDTO.class);
        return simpleTaskDTO;
    }

    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<TaskDTOTwo> createNewTasks(@PathVariable String boardId, @Valid @RequestBody TaskNewDTO task, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        Task createTask = taskService.createNewTasks(boardId, task);
        TaskDTOTwo createdTaskDTO = modelMapper.map(createTask, TaskDTOTwo.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> removeTasks(@PathVariable String boardId, @PathVariable Integer taskId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        TaskDTO deletedTaskDTO = taskService.removeTasks(boardId, taskId);
        return ResponseEntity.ok().body(deletedTaskDTO);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTOTwo> updateTasks(@PathVariable String boardId, @PathVariable Integer taskId, @Valid @RequestBody TaskNewDTO task, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        Task updatedTask = taskService.updateTask(boardId, taskId, task);
        TaskDTOTwo updatedTaskDTO = modelMapper.map(updatedTask, TaskDTOTwo.class);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    // Statuses of board
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<List<StatusDTOTwo>> getAllStatuses(@PathVariable String boardId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.getAllStatuses(boardId));
    }

    @GetMapping("/{boardId}/statuses/{statusId}")
    public TaskStatus getStatusesById(@PathVariable String boardId, @PathVariable Integer statusId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return statusService.getStatusesById(boardId, statusId);
    }

    @GetMapping("/{boardId}/statuses/limit")
    public ResponseEntity<TaskLimit> getStatusesLimit(@PathVariable String boardId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.getStatusesLimit(boardId));
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<StatusDTO> createNewStatuses(@PathVariable String boardId, @Valid @RequestBody StatusDTO status, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        StatusDTO createdStatus = statusService.createNewStatuses(boardId, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatuses(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody TaskStatus task, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.updateStatuses(boardId, statusId, task));
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<Object> removeStatuses(@PathVariable String boardId, @Valid @PathVariable Integer statusId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.removeStatuses(boardId, statusId));
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}/{newId}")
    public ResponseEntity<Object> removeStatusAndReplace(@PathVariable String boardId, @Valid @PathVariable Integer statusId, @PathVariable Integer newId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.transferStatuses(boardId, statusId, newId));
    }

    @PatchMapping("/{boardId}/statuses/maximumtask")
    public ResponseEntity<SimpleLimitDTO> updateLimitTask(
            @PathVariable String boardId,
            @RequestParam @Min(0) @Max(30) Integer maximumTask,
            @RequestParam Boolean isLimit,
            HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);
        return ResponseEntity.ok(statusService.updateLimitTask(boardId, maximumTask, isLimit));
    }


    @GetMapping("/{boardId}/collabs")
    public ResponseEntity<List<CollaboratorDTO>> getCollaboratorsByBoardId(
            @PathVariable String boardId, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);

        // ดึงรายการ collaborator
        List<Collaborator> collaborators = collaboratorService.getCollaboratorsByBoardId(boardId);

        // แปลงรายการ Collaborator เป็น CollaboratorResponseDTO
        List<CollaboratorDTO> responseDTOs = collaborators.stream()
                .map(collaborator -> new CollaboratorDTO(
                        collaborator.getCollaboratorId(),
                        collaborator.getCollaboratorName(),
                        collaborator.getCollaboratorEmail(),
                        collaborator.getAccessLevel(),
                        collaborator.getAddedOn()
                )).toList();

        return ResponseEntity.ok(responseDTOs);
    }

    // Get a specific collaborator by boardId and collaboratorId
    @GetMapping("/{boardId}/collabs/{collaboratorId}")
    public ResponseEntity<CollaboratorDTO> getCollaboratorById(
            @PathVariable String boardId, @PathVariable String collaboratorId, HttpServletRequest request) {
        String userId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);

        // ดึง collaborator หนึ่งคน
        Collaborator collaborator = collaboratorService.getCollaboratorByBoardIdAndCollaboratorId(boardId, collaboratorId);

        // แปลงเป็น CollaboratorResponseDTO
        CollaboratorDTO responseDTO = new CollaboratorDTO(
                collaborator.getCollaboratorId(),
                collaborator.getCollaboratorName(),
                collaborator.getCollaboratorEmail(),
                collaborator.getAccessLevel(),
                collaborator.getAddedOn()
        );

        return ResponseEntity.ok(responseDTO);
    }

    // Add a new collaborator to a board
    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<CollaboratorDTO> addCollaborator(@PathVariable String boardId, @Valid @RequestBody CollaboratorDTO collaboratorRequest, HttpServletRequest request) {
        String userId = getUserId(request);
        String collaboratorId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);

        Collaborator collaborator = collaboratorService.addCollaboratorToBoard(boardId, collaboratorRequest.getEmail(), collaboratorRequest.getAccessRight().name());
        CollaboratorDTO responseDTO = new CollaboratorDTO(
                collaborator.getBoardId(),
                collaborator.getCollaboratorName(),
                collaborator.getCollaboratorEmail(),
                collaborator.getAccessLevel(),
                collaborator.getAddedOn()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @PatchMapping("/{boardId}/collabs/{collabId}")
    public ResponseEntity<CollaboratorDTO> updateCollaboratorAccessRight(
            @PathVariable String boardId,
            @PathVariable String collabId,
            @Valid @RequestBody CollaboratorDTO collaboratorRequest,
            HttpServletRequest request) {

        String userId = getUserId(request);

        // ใช้ collabId แทน collaboratorId
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collabId);

        // ใช้ collabId แทน collaboratorId
        Collaborator updatedCollaborator = collaboratorService.updateCollaboratorAccessRight(
                boardId, collabId, collaboratorRequest.getAccessRight().name());

        CollaboratorDTO responseDTO = new CollaboratorDTO(
                updatedCollaborator.getCollaboratorId(),
                updatedCollaborator.getCollaboratorName(),
                updatedCollaborator.getCollaboratorEmail(),
                updatedCollaborator.getAccessLevel(),
                updatedCollaborator.getAddedOn()
        );

        return ResponseEntity.ok(responseDTO);
    }


    @DeleteMapping("/{boardId}/collabs/{collaboratorId}")
    public ResponseEntity<CollaboratorDTO> removeCollaborator(
            @PathVariable String boardId,
            @PathVariable String collaboratorId,
            HttpServletRequest request) {
        String userId = getUserId(request);
        boardService.checkOwnerAndVisibility(boardId, userId, request.getMethod(), collaboratorId);

        // ลบ collaborator
        Collaborator removedCollaborator = collaboratorService.removeCollaborator(boardId, collaboratorId);

        // แปลง collaborator ที่ถูกลบเป็น DTO
        CollaboratorDTO responseDTO = new CollaboratorDTO(
                removedCollaborator.getCollaboratorId(),
                removedCollaborator.getCollaboratorName(),
                removedCollaborator.getCollaboratorEmail(),
                removedCollaborator.getAccessLevel(),
                removedCollaborator.getAddedOn()
        );

        return ResponseEntity.ok(responseDTO);
    }






}




