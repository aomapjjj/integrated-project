package sit.int221.servicetasksj3.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardRequestDTO;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.services.BoardService;

import java.util.List;

@RestController
@RequestMapping("/boards")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class BoardController {
    @Autowired
    private BoardService boardService;

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
}