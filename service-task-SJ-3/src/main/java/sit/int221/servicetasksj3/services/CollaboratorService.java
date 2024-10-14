package sit.int221.servicetasksj3.services;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.boardsDTO.CollaboratorDTO;
import sit.int221.servicetasksj3.entities.AccessRight;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.Collaborator;
import sit.int221.servicetasksj3.exceptions.ConflictException;
import sit.int221.servicetasksj3.exceptions.ForbiddenException;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.ValidationException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.CollaboratorRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaboratorService {
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository usersRepository;


    public boolean isCollaborator(String boardId, String userId) {
        return collaboratorRepository.existsByBoardIdAndCollaboratorId(boardId, userId);
    }

    public List<CollaboratorDTO> getCollaboratorsByBoardId(String boardId) {
        boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));
        return collaboratorRepository.findByBoardId(boardId).stream()
                .map(c -> new CollaboratorDTO(
                        c.getCollaboratorId(),
                        c.getCollaboratorName(),
                        c.getCollaboratorEmail(),
                        c.getAccessLevel(),
                        c.getAddedOn()
                ))
                .toList();
    }

    public CollaboratorDTO getCollaboratorByBoardIdAndCollaboratorId(String boardId, String collaboratorId) {
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found");
        }
        return new CollaboratorDTO(
                collaborator.getCollaboratorId(),
                collaborator.getCollaboratorName(),
                collaborator.getCollaboratorEmail(),
                collaborator.getAccessLevel(),
                collaborator.getAddedOn()
        );
    }


    public CollaboratorDTO addCollaboratorToBoard(String boardId, String collaboratorEmail, String accessRight) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        Users user = usersRepository.findByEmail(collaboratorEmail)
                .orElseThrow(() -> new ItemNotFoundException("User not found with email: " + collaboratorEmail));

        // ตรวจสอบว่า email นี้เป็นของเจ้าของบอร์ดหรือไม่
        if (user.getOid().equals(board.getOwnerId())) {
            throw new ConflictException("The collaborator email belongs to the board owner");
        }

        // ตรวจสอบว่าผู้ใช้นี้เป็น collaborator ในบอร์ดนี้แล้วหรือไม่
        if (collaboratorRepository.existsByBoardIdAndCollaboratorEmail(boardId, collaboratorEmail)) {
            throw new ConflictException("The collaborator already exists for this board");
        }

        Collaborator collaborator = new Collaborator();
        collaborator.setCollaboratorId(user.getOid());
        collaborator.setCollaboratorName(user.getName());
        collaborator.setBoardId(boardId);
        collaborator.setOwnerId(board.getOwnerId());
        collaborator.setCollaboratorEmail(collaboratorEmail);
        collaborator.setAccessLevel(AccessRight.valueOf(accessRight));
        collaborator.setAddedOn(new Timestamp(System.currentTimeMillis()));
        collaboratorRepository.save(collaborator);

        return new CollaboratorDTO(
                collaborator.getCollaboratorId(),
                collaborator.getCollaboratorName(),
                collaborator.getCollaboratorEmail(),
                collaborator.getAccessLevel(),
                collaborator.getAddedOn()
        );
    }
    public Collaborator updateCollaboratorAccessRight(String boardId, String collaboratorId, String newAccessRight) {


        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));


//        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


//        if (!board.getOwnerId().equals(currentUser.getOid())) {
//            throw new ForbiddenException("Only the owner can change collaborator access rights");
//        }


        Collaborator collaborator = collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found");
        }


        AccessRight accessRight;
        try {
            accessRight = AccessRight.valueOf(newAccessRight.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid access right. Only READ and WRITE are allowed.");
        }


        collaborator.setAccessLevel(accessRight);


        return collaboratorRepository.save(collaborator);
    }

    public Collaborator removeCollaborator(String boardId, String collaboratorId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

//        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Allow removal if the current user is either the owner or the collaborator themself
//        if (!board.getOwnerId().equals(currentUser.getOid()) && !currentUser.getOid().equals(collaboratorId)) {
//            throw new ForbiddenException("Only the owner or the collaborator can remove themselves from this board");
//        }

        // Check if the collaborator exists
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found");
        }

        // Proceed with removal
        collaboratorRepository.delete(collaborator);

        // Return the removed collaborator
        return collaborator;
    }





}