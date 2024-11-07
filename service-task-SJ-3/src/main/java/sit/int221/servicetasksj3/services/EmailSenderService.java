package sit.int221.servicetasksj3.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.emailDTO.EmailResponseDTO;
import sit.int221.servicetasksj3.exceptions.ConflictException;
import sit.int221.servicetasksj3.exceptions.ForbiddenException;
import sit.int221.servicetasksj3.repositories.CollaboratorRepository;

import java.io.UnsupportedEncodingException;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private BoardService boardService;

    @Value("${file.file-service-host}")
    private String hostName;

    public EmailResponseDTO sendEmail(String boardId, String email, String inviterName, String accessRight, String boardName) throws MessagingException, UnsupportedEncodingException {
        String subject = inviterName + " has invited you to collaborate with " + accessRight + " access right on " + boardName + " board";
        String boardUrl = "https://" + hostName + "/board/" + boardId + "/collab/invitations";
        String content = generateEmailContent(inviterName, boardName, accessRight, boardUrl);

        if (!boardService.isBoardOwner(boardId)) {
            throw new ForbiddenException("Only the board owner can send emails");
        }

        if (collaboratorRepository.existsByBoardIdAndCollaboratorEmail(boardId, email)) {
            throw new ConflictException("The email already exists for this board");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("noreply@intproj23.sit.kmutt.ac.th", "ITBKK-SJ3");
        helper.setReplyTo("noreply@intproj23.sit.kmutt.ac.th", "DO NOT REPLY");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        try {
            mailSender.send(message);
            return new EmailResponseDTO("Email sent successfully.");
        } catch (Exception e) {
            return new EmailResponseDTO("We could not send an email to " + email + ", they can accept the invitation at " + boardUrl);
        }
    }

    private String generateEmailContent(String inviterName, String boardName, String accessRight, String boardUrl) {
        return "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                + "<h2 style='color: #0066cc;'>You have been invited to collaborate on " + boardName + "!</h2>"
                + "<p>Hello,</p>"
                + "<p><strong>" + inviterName + "</strong> has invited you to collaborate on the <strong>" + boardName + "</strong> board with <strong>" + accessRight + "</strong> access rights.</p>"
                + "<p>Please click the button below to accept the invitation:</p>"
                + "<a href='" + boardUrl + "' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #28a745; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Accept Invitation</a>"
                + "<p>If you don’t want to join this board, you can simply ignore this email.</p>"
                + "<hr style='border: none; border-top: 1px solid #ddd;' />"
                + "<p style='font-size: 12px; color: #777;'>This is an automated message, please do not reply.</p>"
                + "</div>";
    }
}