package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.collaboratorDTO.CollaboratorDTO;
import sit.int221.servicetasksj3.entities.*;
import sit.int221.servicetasksj3.exceptions.*;
import sit.int221.servicetasksj3.repositories.*;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CollaboratorService {
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository usersRepository;

    public boolean isCollaborator(String boardId, String userId) {
        if (!collaboratorRepository.existsByBoardIdAndCollaboratorIdAndStatus(boardId, userId, CollabStatus.ACCEPTED)){
            return false;
        } else {
            return collaboratorRepository.existsByBoardIdAndCollaboratorIdAndStatus(boardId, userId, CollabStatus.ACCEPTED);
        }
    }

    public boolean hasWriteAccess(String boardId, String userId){
        return collaboratorRepository.existsByBoardIdAndCollaboratorIdAndAccessLevel(boardId, userId, AccessRight.WRITE);
    }

    public List<CollaboratorDTO> getCollaboratorsByBoardId(String boardId) {
        boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));
        return collaboratorRepository.findByBoardId(boardId).stream()
                .map(c -> {
                    String name = c.getCollaboratorName();
                    if (c.getStatus() == CollabStatus.PENDING) {
                        name += " (Pending Invite)";
                    }
                    return new CollaboratorDTO(
                            c.getCollaboratorId(),
                            name,
                            c.getCollaboratorEmail(),
                            c.getAccessLevel(),
                            c.getAddedOn(),
                            c.getStatus()
                    );
                })
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
                collaborator.getAddedOn(),
                collaborator.getStatus()
        );
    }

    public CollaboratorDTO addCollaboratorToBoard(String boardId, String collaboratorEmail, String accessRight, String status) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        Users user = usersRepository.findByEmail(collaboratorEmail)
                .orElseThrow(() -> new ItemNotFoundException("User not found with email: " + collaboratorEmail));

        if (user.getOid().equals(board.getOwnerId())) {
            throw new ConflictException("The collaborator email belongs to the board owner");
        }

        Collaborator existingCollaborator = collaboratorRepository.findByBoardIdAndCollaboratorEmail(boardId, collaboratorEmail).orElse(null);

        if (existingCollaborator != null) {
            if (existingCollaborator.getStatus() == CollabStatus.PENDING) {
                throw new ConflictException("The user is already a collaborator or pending collaborator of this board");
            } else {
                throw new ConflictException("The collaborator already exists for this board");
            }
        }

        Collaborator collaborator = new Collaborator();
        collaborator.setCollaboratorId(user.getOid());
        collaborator.setCollaboratorName(user.getName());
        collaborator.setBoardId(boardId);
        collaborator.setOwnerId(board.getOwnerId());
        collaborator.setCollaboratorEmail(collaboratorEmail);
        collaborator.setAccessLevel(AccessRight.valueOf(accessRight));
        collaborator.setAddedOn(new Timestamp(System.currentTimeMillis()));
        collaborator.setStatus(CollabStatus.valueOf(status));
        collaboratorRepository.save(collaborator);

        return new CollaboratorDTO(
                collaborator.getCollaboratorId(),
                collaborator.getCollaboratorName(),
                collaborator.getCollaboratorEmail(),
                collaborator.getAccessLevel(),
                collaborator.getAddedOn(),
                collaborator.getStatus()
        );
    }

    public Collaborator updateCollaboratorAccessRight(String boardId, String collaboratorId, String newAccessRight) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

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

    public Collaborator updateCollaboratorStatus(String boardId, String collaboratorId, String newStatus) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        Collaborator collaborator = collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found");
        }

        CollabStatus collabStatus;
        try {
            collabStatus = CollabStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid access right. Only READ and WRITE are allowed.");
        }

        collaborator.setStatus(collabStatus);
        return collaboratorRepository.save(collaborator);
    }

    public Collaborator removeCollaborator(String boardId, String collaboratorId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        Collaborator collaborator = collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found");
        }

        collaboratorRepository.delete(collaborator);
        return collaborator;
    }
}