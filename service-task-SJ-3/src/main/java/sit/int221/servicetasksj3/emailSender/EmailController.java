package sit.int221.servicetasksj3.emailSender;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )
public class EmailController {

    private final EmailSenderService emailSenderService;

    @Autowired
    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody EmailDTO request) throws MessagingException, UnsupportedEncodingException {
        String boardId = request.getBoardId();
        String recipientEmail = request.getEmail();
        String inviterName = request.getInviterName();
        String boardName = request.getBoardName();
        String accessRight = request.getAccessRight();
        String boardUrl = request.getBoardUrl();

        String subject = inviterName + " has invited you to collaborate with " + accessRight + " access right on " + boardName + " board";
        String body = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                + "<h2 style='color: #0066cc;'>You have been invited to collaborate on " + boardName + "!</h2>"
                + "<p>Hello,</p>"
                + "<p><strong>" + inviterName + "</strong> has invited you to collaborate on the <strong>" + boardName + "</strong> board with <strong>" + accessRight + "</strong> access rights.</p>"
                + "<p>Please click the button below to accept the invitation:</p>"
                + "<a href='" + boardUrl + "' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #28a745; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Accept Invitation</a>"
                + "<p>If you don’t want to join this board, you can simply ignore this email.</p>"
                + "<hr style='border: none; border-top: 1px solid #ddd;' />"
                + "<p style='font-size: 12px; color: #777;'>This is an automated message, please do not reply.</p>"
                + "</div>";

        try {
            emailSenderService.sendEmail(boardId, recipientEmail, subject, body);
            return "Email sent successfully.";
        } catch (MessagingException | UnsupportedEncodingException e) {
            return "Failed to send email. Error: " + e.getMessage();
        }
    }

}