package sit.int221.servicetasksj3.services;

import io.viascom.nanoid.NanoId;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.boardsDTO.*;
import sit.int221.servicetasksj3.dtos.collaboratorDTO.CollaboratorDTO;
import sit.int221.servicetasksj3.entities.*;
import sit.int221.servicetasksj3.exceptions.*;
import sit.int221.servicetasksj3.repositories.*;
import sit.int221.servicetasksj3.sharedatabase.entities.*;
import sit.int221.servicetasksj3.sharedatabase.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private MicrosoftDetailRepository microsoftDetailRepository;

    public String generateUniqueBoardId() {
        String boardId;
        do {
            boardId = NanoId.generate(10);
        } while (boardRepository.existsById(boardId));
        return boardId;
    }

    public void checkOwnerAndVisibility(String boardId, String userId, String requestMethod, String collaboratorId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        boolean isOwner = board.getOwnerId().equals(userId);
        boolean isPublic = board.getVisibility() == Visibility.PUBLIC;
        boolean isPrivate = board.getVisibility() == Visibility.PRIVATE;
        boolean isCollaborator = collaboratorId != null && collaboratorRepository.existsByBoardIdAndCollaboratorId(boardId, collaboratorId);

        if (board != null) {
            if (isPublic || isOwner || isCollaborator) {
                return;
            } else {
                if (!isCollaborator) {
                    throw new ItemNotFoundException("The board exists, but the user is not authorized to access this board.");
                }
                throw new ForbiddenException("The board exists, but the user is not authorized to access this board.");
            }
        }
        if (userId == null) {
            if (isPrivate) {
                throw new ForbiddenException("The board exists, but the user is not the owner and the board is private.");
            }
            if (isPublic && !requestMethod.equals("GET")) {
                throw new ForbiddenException("The board exists, but the user is not authorized for this action on a public board.");
            }
            return;
        }
        if (isOwner) {
            return;
        }
        if (isPublic) {
            if (requestMethod.equals("POST")) {
                return;
            }
            if (!requestMethod.equals("GET")) {
                throw new ForbiddenException("The board exists, but the user is not the owner and the board is public.");
            }
            return;
        }
        throw new ForbiddenException("The board exists, but the user is not the owner and the board is private.");
    }

    public Boolean isBoardPublic(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));
        return board.getVisibility().equals(Visibility.PUBLIC);
    }

    public Boolean isBoardOwner(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));
        return board.getOwnerId().equals(((AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getOid());
    }

    public boolean boardExists(String boardId) {
        if (boardId == null) {
            throw new ValidationException("Board ID is required");
        }
        return boardRepository.existsById(boardId);
    }

    // Get board IDs by owner
    public List<BoardResponseDTO> getBoardIdByOwner() {
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String oid = currentUser.getOid();

        List<Board> boards = boardRepository.findAllByUserIdOrCollaboratorId(oid);
        System.out.println("Boards found: " + boards.size());

        return boards.stream().map(board -> {
            BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);
            BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
            ownerDTO.setOid(board.getOwnerId());
            ownerDTO.setName(
                    userRepository.findById(board.getOwnerId())
                            .map(Users::getName)
                            .orElseGet(() ->
                                    microsoftDetailRepository.findById(board.getOwnerId())
                                            .map(MicrosoftDetail::getDisplayName)
                                            .orElseThrow(() -> new ItemNotFoundException(
                                                    "User not found with ID: " + board.getOwnerId()
                                            ))
                            )
            );

            boardResponse.setOwner(ownerDTO);
            return boardResponse;
        }).collect(Collectors.toList());
    }

    public boolean isPending(String boardId, String userId) {
        if (collaboratorRepository.existsByBoardIdAndCollaboratorIdAndStatus(boardId, userId, CollabStatus.PENDING)){
            return true;
        }else {
            return false;
        }
    }

    public Map<String, Object> getBoardsByOwner() {
        List<BoardResponseDTO> boardIds = getBoardIdByOwner();
        Map<String, Object> response = new HashMap<>();

        if (boardIds.isEmpty()) {
            response.put("boards", new ArrayList<BoardResponseDTO>());
            response.put("invites", new ArrayList<CollaboratorDTO>());
            response.put("collabs", new ArrayList<CollaboratorDTO>());
            return response;
        }

        List<CollaboratorDTO> invites = new ArrayList<>();
        List<CollaboratorDTO> collabs = new ArrayList<>();

        for (BoardResponseDTO board : boardIds) {
            List<CollaboratorDTO> collaborators = collaboratorService.getCollaboratorsByBoardId(board.getId());

            for (CollaboratorDTO collaborator : collaborators) {
                if (collaborator.getStatus() == CollabStatus.PENDING) {
                    invites.add(collaborator);
                } else if (collaborator.getStatus() == CollabStatus.ACCEPTED &&
                        (collaborator.getAccessRight() == AccessRight.READ || collaborator.getAccessRight() == AccessRight.WRITE)) {
                    collabs.add(collaborator);
                }
            }

            board.setCollaborators(collaborators);
        }

        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getOid();

        List<BoardResponseDTO> ownerBoards = boardIds.stream()
                .filter(board -> board.getOwner().getOid().equals(userId))
                .collect(Collectors.toList());

        List<BoardResponseDTO> collaboratorBoards = boardIds.stream()
                .filter(board -> !board.getOwner().getOid().equals(userId))
                .collect(Collectors.toList());

        response.put("boards", ownerBoards);
        response.put("collabs", collaboratorBoards);
        response.put("invites", invites);

        return response;
    }


    // Get board by IDs
    public BoardResponseDTO getBoardById(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + id));


        BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(board.getOwnerId());
        ownerDTO.setName(userRepository.findById(board.getOwnerId())
                .map(Users::getName)
                .orElseGet(() ->
                        microsoftDetailRepository.findById(board.getOwnerId())
                                .map(MicrosoftDetail::getDisplayName)
                                .orElseThrow(() -> new ItemNotFoundException(
                                        "Owner not found with ID: " + board.getOwnerId()
                                ))
                ));
        boardResponse.setOwner(ownerDTO);
        return boardResponse;
    }

    // Create a new board
    public BoardResponseDTO createNewBoard(BoardRequestDTO boardRequest) {
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String oid = currentUser.getOid();

        Board board = new Board();
        board.setId(generateUniqueBoardId());
        board.setOwnerId(oid);
        board.setName(boardRequest.getName());

        Board newBoard = boardRepository.save(board);

        // Add default statuses
        List<TaskStatus> statuses = new ArrayList<>();
        TaskStatus status1 = new TaskStatus("No Status", "A status has not been assigned", newBoard.getId());
        TaskStatus status2 = new TaskStatus("To Do", "The task is included in the project", newBoard.getId());
        TaskStatus status3 = new TaskStatus("Doing", "The task is being worked on", newBoard.getId());
        TaskStatus status4 = new TaskStatus("Done", "The task has been completed", newBoard.getId());
        statuses.add(status1);
        statuses.add(status2);
        statuses.add(status3);
        statuses.add(status4);
        statusRepository.saveAll(statuses);

        TaskLimit limit = new TaskLimit(10, false, newBoard.getId());
        limitRepository.save(limit);

        // Map owner information
        BoardResponseDTO boardResponse = modelMapper.map(newBoard, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(currentUser.getOid());
        ownerDTO.setName(currentUser.getName());
        boardResponse.setOwner(ownerDTO);

        return boardResponse;
    }

    // Delete
    @Transactional
    public Board removeBoard(String boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ValidationException("Board not found with ID: " + boardId));
        try {
            boardRepository.delete(board);
            return board;
        } catch (Exception e) {
            throw new ValidationException("Error occurred while deleting the board: " + e.getMessage());
        }
    }

    // Edit
    @Transactional
    public BoardResponseDTO editBoard(String boardId, BoardRequestDTO boardRequest) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        board.setName(boardRequest.getName());

        Board updatedBoard = boardRepository.save(board);

        BoardResponseDTO boardResponse = modelMapper.map(updatedBoard, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(currentUser.getOid());
        ownerDTO.setName(currentUser.getName());
        boardResponse.setOwner(ownerDTO);

        return boardResponse;
    }

    // Edit visibility
    @Transactional
    public VisibilityDTO editBoardVisibility(String boardId, VisibilityDTO visibility) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        board.setVisibility(visibility.getVisibility());
        boardRepository.save(board);

        return visibility;
    }
}