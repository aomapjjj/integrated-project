package sit.int221.servicetasksj3.services;

import io.viascom.nanoid.NanoId;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.boardsDTO.*;
import sit.int221.servicetasksj3.entities.*;
import sit.int221.servicetasksj3.exceptions.*;
import sit.int221.servicetasksj3.repositories.*;
import sit.int221.servicetasksj3.sharedatabase.entities.*;
import sit.int221.servicetasksj3.sharedatabase.repositories.*;

import java.util.ArrayList;
import java.util.List;
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
    private ListMapper listMapper;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;


    private String generateUniqueBoardId() {
        String boardId;
        do {
            boardId = NanoId.generate(10);
        } while (boardRepository.existsById(boardId)); // ตรวจสอบว่ามี id นี้ในฐานข้อมูลแล้วหรือยัง
        return boardId;
    }

    public void checkOwnerAndVisibility(String boardId, String userId, String requestMethod , String collaboratorId) {
        // ค้นหา Board ตาม ID
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));


        boolean isOwner = board.getOwnerId().equals(userId);
        boolean isPublic = board.getVisibility() == Visibility.PUBLIC;
        boolean isPrivate = board.getVisibility() == Visibility.PRIVATE;
        boolean isCollaborator = collaboratorId != null && collaboratorRepository.existsByBoardIdAndCollaboratorId(boardId, collaboratorId);


        // กรณีที่ Board มีอยู่แล้ว (board($id).exists)
        if (board != null) {
            // ตรวจสอบว่าเป็นเจ้าของ, public, หรือ collaborator
            if (isPublic || isOwner || isCollaborator) {
                return; // อนุญาตให้เข้าถึง
            } else {
                // ไม่ตรงตามเงื่อนไข public, owner, หรือ collaborator
                throw new ForbiddenException("The board exists, but the user is not authorized to access this board.");
            }
        }

        // กรณีที่ผู้ใช้ไม่เข้าสู่ระบบ
        if (userId == null) {
            // ผู้ใช้ไม่เข้าสู่ระบบและพยายามเข้าถึงบอร์ดส่วนตัว
            if (isPrivate) {
                throw new ForbiddenException("The board exists, but the user is not the owner and the board is private.");
            }
            // ผู้ใช้ไม่เข้าสู่ระบบและเข้าถึงบอร์ด public (เฉพาะ GET)
            if (isPublic && !requestMethod.equals("GET")) {
                throw new ForbiddenException("The board exists, but the user is not authorized for this action on a public board.");
            }
            return; // อนุญาตให้เข้าถึง GET ในกรณีบอร์ด public
        }

        // กรณีที่ผู้ใช้เข้าสู่ระบบ
        if (isOwner ) {
            return; // ผู้ใช้เป็นเจ้าของ สามารถเข้าถึงได้ทุกกรณี
        }

        // ผู้ใช้ไม่ใช่เจ้าของ
        if (isPublic) {
            // บอร์ดเป็น public และการกระทำเป็น POST เพื่อเพิ่ม collaborator (อนุญาตได้)
            if (requestMethod.equals("POST")) {
                return;
            }
            // บอร์ดเป็น public และการกระทำเป็นอย่างอื่นที่ไม่ใช่ GET (ห้ามทำ)
            if (!requestMethod.equals("GET")) {
                throw new ForbiddenException("The board exists, but the user is not the owner and the board is public.");
            }
            return; // การกระทำเป็น GET และบอร์ดเป็น public อนุญาตให้เข้าถึง
        }


        // ผู้ใช้ไม่ใช่เจ้าของและบอร์ดเป็น private (ห้ามทุกการกระทำ)
        throw new ForbiddenException("The board exists, but the user is not the owner and the board is private.");
    }
    public  Boolean isBoardPublic(String boardId) {
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

//    public boolean isCollaborator(String boardId, String oid) {
//        return collabRepository.existsById(new CollabId(boardId, oid));
//    }


    // Get board IDs by owner
    public List<BoardResponseDTO> getBoardIdByOwner() {
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String oid = currentUser.getOid();
        System.out.println("Current User OID: " + oid);

        List<Board> boards = boardRepository.findAllByUserIdOrCollaboratorId(oid);
        System.out.println("Boards found: " + boards.size());


        return boards.stream().map(board -> {
            BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);

            BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
            ownerDTO.setOid(board.getOwnerId());
            ownerDTO.setName(userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("User not found with ID: " + board.getOwnerId())).getName());
            boardResponse.setOwner(ownerDTO);

            return boardResponse;
        }).collect(Collectors.toList());
    }

    // Get board by IDs
    public BoardResponseDTO getBoardById(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + id));
        Users owner = userRepository.findById(board.getOwnerId())
                .orElseThrow(() -> new ItemNotFoundException("Owner not found with ID: " + board.getOwnerId()));

        BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(board.getOwnerId());
        ownerDTO.setName(owner.getName());
        boardResponse.setOwner(ownerDTO);
        return boardResponse;
    }

    // Create a new board
    public BoardResponseDTO createNewBoard(BoardRequestDTO boardRequest) {
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (currentUser == null) {
//            throw new UnauthorizedException("User not authenticated");
//        }

        String oid = currentUser.getOid();

        Board board = new Board();
        board.setId(generateUniqueBoardId());
        board.setOwnerId(oid);
        board.setName(boardRequest.getName());
//
        Board newBoard = boardRepository.save(board);

        // Add default statuses
        List<TaskStatus> statuses = new ArrayList<>();
        TaskStatus status1 = new TaskStatus("No Status","A status has not been assigned", newBoard.getId());
        TaskStatus status2 = new TaskStatus("To Do","The task is included in the project", newBoard.getId());
        TaskStatus status3 = new TaskStatus("Doing","The task is being worked on", newBoard.getId());
        TaskStatus status4 = new TaskStatus("Done","The task has been completed", newBoard.getId());
        statuses.add(status1);
        statuses.add(status2);
        statuses.add(status3);
        statuses.add(status4);
        statusRepository.saveAll(statuses);

        TaskLimit limit = new TaskLimit(10,false, newBoard.getId());
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
    public Board removeBoard(String boardId){
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

        // Check if the user is the owner of the board
//        if (!board.getOwnerId().equals(currentUser.getOid())) {
//            throw new ForbiddenException("User is not authorized to edit this board");
//        }

        board.setName(boardRequest.getName());
//        board.setVisibility(boardRequest.getVisibility());

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

//        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // ตรวจสอบว่า user เป็นเจ้าของ board หรือไม่
//        if (!board.getOwnerId().equals(currentUser.getOid())) {
//            throw new ForbiddenException("User is not authorized to edit this board");
//        }

        board.setVisibility(visibility.getVisibility());
        boardRepository.save(board);

        return visibility;
    }
}