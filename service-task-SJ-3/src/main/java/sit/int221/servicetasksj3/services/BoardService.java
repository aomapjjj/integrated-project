package sit.int221.servicetasksj3.services;


import io.viascom.nanoid.NanoId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardRequestDTO;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;


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

    private String generateUniqueBoardId() {
        return NanoId.generate(10);
    }

    // Get board IDs by owner
    public List<BoardResponseDTO> getBoardIdByOwner() {
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String oid = currentUser.getOid();
        System.out.println("Current User OID: " + oid);

        List<Board> boards = boardRepository.findByOwnerId(oid);
        System.out.println("Boards found: " + boards.size());

        return boards.stream().map(board -> {
            BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);

            BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
            ownerDTO.setOid(currentUser.getOid());
            ownerDTO.setName(currentUser.getName());

            boardResponse.setOwner(ownerDTO);
            return boardResponse;
        }).collect(Collectors.toList());
    }

    // Get board by IDs
    public BoardResponseDTO getBoardById(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + id));

        BoardResponseDTO boardResponse = modelMapper.map(board, BoardResponseDTO.class);
        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(currentUser.getOid());
        ownerDTO.setName(currentUser.getName());

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
        List<TaskStatus> statuses = new ArrayList<>();
        TaskStatus status1 = new TaskStatus("No Status","A status has not been assigned", newBoard.getId());
        TaskStatus status2 = new TaskStatus("To Do","The task is included in the project", newBoard.getId());
        TaskStatus status3 = new TaskStatus("In Progress","The task is being worked on", newBoard.getId());
        TaskStatus status4 = new TaskStatus("Done","The task has been completed", newBoard.getId());
        statuses.add(status1);
        statuses.add(status2);
        statuses.add(status3);
        statuses.add(status4);
        statusRepository.saveAll(statuses);

        // Map owner information
        BoardResponseDTO boardResponse = modelMapper.map(newBoard, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(currentUser.getOid());
        ownerDTO.setName(currentUser.getName());
        boardResponse.setOwner(ownerDTO);

        return boardResponse;
    }
}


