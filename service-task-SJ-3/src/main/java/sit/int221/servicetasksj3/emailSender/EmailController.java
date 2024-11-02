package sit.int221.servicetasksj3.emailSender;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmailController {

    private final EmailSenderService emailSenderService;

    @Autowired
    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody EmailDTO request) throws MessagingException, UnsupportedEncodingException {
        String recipientEmail = request.getEmail();
        String inviterName = request.getInviterName();
        String boardName = request.getBoardName();
        String accessRight = request.getAccessRight();
        String boardUrl = request.getBoardUrl();

        String subject = inviterName + " has invited you to collaborate with " + accessRight + " access right on " + boardName + " board";
        String body = "You have been invited to collaborate on the board \"" + boardName + "\" with " + accessRight + " access rights.\n" +
                "Please accept the invitation by clicking the link below:\n" +
                boardUrl;

        try {
            emailSenderService.sendEmail(recipientEmail, subject, body);
            return "Email sent successfully.";
        } catch (MessagingException | UnsupportedEncodingException e) {
            return "Failed to send email. Error: " + e.getMessage();
        }
    }

}