package sit.int221.servicetasksj3.emailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import sit.int221.servicetasksj3.exceptions.ConflictException;
import sit.int221.servicetasksj3.exceptions.ForbiddenException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.CollaboratorRepository;
import sit.int221.servicetasksj3.services.BoardService;
import sit.int221.servicetasksj3.services.CollaboratorService;

import java.io.UnsupportedEncodingException;
@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private BoardService boardService;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String boardId , String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        if (!boardService.isBoardOwner(boardId)) {
            throw new ForbiddenException("Only the board owner can send emails.");
        }

        if (collaboratorRepository.existsByBoardIdAndCollaboratorEmail(boardId, email)) {
            throw new ConflictException("The email already exists for this board.");
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("noreply@intproj23.sit.kmutt.ac.th", "No Reply - ITBKK-SJ-3");
        helper.setReplyTo("noreply@intproj23.sit.kmutt.ac.th");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}