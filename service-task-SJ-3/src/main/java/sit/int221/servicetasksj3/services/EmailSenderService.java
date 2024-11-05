package sit.int221.servicetasksj3.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.dtos.emailDTO.EmailRequestDTO;
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

    public EmailResponseDTO sendEmail(EmailRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        String boardId = request.getBoardId();
        String email = request.getEmail();
        String subject = request.getInviterName() + " has invited you to collaborate with " + request.getAccessRight() + " access right on " + request.getBoardName() + " board";
        String content = generateEmailContent(request);

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
            return new EmailResponseDTO("We could not send an email to " + request.getEmail() + ", they can accept the invitation at " + request.getBoardUrl());
        }
    }

    private String generateEmailContent(EmailRequestDTO request) {
        return "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                + "<h2 style='color: #0066cc;'>You have been invited to collaborate on " + request.getBoardName() + "!</h2>"
                + "<p>Hello,</p>"
                + "<p><strong>" + request.getInviterName() + "</strong> has invited you to collaborate on the <strong>" + request.getBoardName() + "</strong> board with <strong>" + request.getAccessRight() + "</strong> access rights.</p>"
                + "<p>Please click the button below to accept the invitation:</p>"
                + "<a href='" + request.getBoardUrl() + "' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #28a745; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Accept Invitation</a>"
                + "<p>If you don’t want to join this board, you can simply ignore this email.</p>"
                + "<hr style='border: none; border-top: 1px solid #ddd;' />"
                + "<p style='font-size: 12px; color: #777;'>This is an automated message, please do not reply.</p>"
                + "</div>";
    }
}