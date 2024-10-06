package sit.int221.servicetasksj3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.entities.AccessRight;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.Collaborator;
import sit.int221.servicetasksj3.exceptions.ForbiddenException;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.CollaboratorRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CollaboratorService {
    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private BoardRepository boardRepository;

    private UserRepository usersRepository;

    public boolean isEmailInITBKKShared(String email) {

        return usersRepository.findByEmail(email).isPresent();
    }

    // ดึงรายชื่อ Collaborators ทั้งหมดของ Board
    public List<Collaborator> getCollaboratorsByBoardId(String boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        return collaboratorRepository.findByBoardId(boardId);
    }

  
    public Collaborator getCollaboratorByBoardIdAndCollaboratorId(String boardId, String collaboratorId) {
        return collaboratorRepository.findByBoardIdAndCollaboratorId(boardId, collaboratorId);
    }


    public Collaborator addCollaboratorToBoard(String boardId, String collaboratorEmail, String accessRight) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        AuthUser currentUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(board.getOwnerId().equals(currentUser.getOid())){
            throw new ForbiddenException("The board exists, but the user is the owner cannot be collaborator");
        }

        Collaborator collaborator = new Collaborator();
        collaborator.setCollaboratorId(currentUser.getOid());
        collaborator.setCollaboratorName(currentUser.getName());
        collaborator.setBoardId(boardId);
        collaborator.setOwnerId(board.getOwnerId());
        collaborator.setAccessLevel(AccessRight.valueOf(accessRight));
        collaborator.setCollaboratorEmail(collaboratorEmail);
        collaborator.setAddedOn(new Timestamp(System.currentTimeMillis()));

        return collaboratorRepository.save(collaborator);
    }
}