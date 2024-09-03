package sit.int221.servicetasksj3.services;


import io.viascom.nanoid.NanoId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardRequestDTO;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    private String generateUniqueBoardId() {
        return NanoId.generate( 10);
    }

    // Get board IDs by owner
    public List<BoardResponseDTO> getBoardIdByOwner() {
            AuthUser currentUser = sit.int221.servicetasksj3.utils.SecurityUtil.getCurrentUserDetails();
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
        AuthUser currentUser = sit.int221.servicetasksj3.utils.SecurityUtil.getCurrentUserDetails();

        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
        ownerDTO.setOid(currentUser.getOid());
        ownerDTO.setName(currentUser.getName());

        boardResponse.setOwner(ownerDTO);
        return boardResponse;
    }

    // Create a new board
    public BoardResponseDTO createNewBoard(BoardRequestDTO boardRequest) {
        AuthUser currentUser = sit.int221.servicetasksj3.utils.SecurityUtil.getCurrentUserDetails();
        String oid = currentUser.getOid();

        Board newBoard = new Board();
            newBoard.setId(generateUniqueBoardId());
            newBoard.setOwnerId(oid);
            newBoard.setName(boardRequest.getName());
        boardRepository.save(newBoard);

        // Map owner information
        BoardResponseDTO boardResponse = modelMapper.map(newBoard, BoardResponseDTO.class);
        BoardResponseDTO.OwnerDTO ownerDTO = new BoardResponseDTO.OwnerDTO();
            ownerDTO.setOid(currentUser.getOid());
            ownerDTO.setName(currentUser.getName());
        boardResponse.setOwner(ownerDTO);

        return boardResponse;
    }
}


