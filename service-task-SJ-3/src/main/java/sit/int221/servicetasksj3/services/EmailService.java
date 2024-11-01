//package sit.int221.servicetasksj3.services;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
//
//    public void sendInvitationEmail(String to, String inviterName, String boardName, String accessRight, String boardId) {
//        String subject = inviterName + " has invited you to collaborate with " + accessRight + " access right on " + boardName + " board";
//        String body = "You have been invited to collaborate on the board \"" + boardName + "\" with " + accessRight + " access rights.\n" +
//                "Please accept the invitation by clicking the link below:\n" +
//                "http://localhost:8080/board/" + boardId + "/collab/invitations";
//        // http://intproj23.sit.kmutt.ac.th/board/
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("noreply@intproj23.sit.kmutt.ac.th");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//
////        mailSender.send(message);
//
//        try {
//            mailSender.send(message);
//            logger.info("Invitation email sent successfully to {}", to);
//        } catch (Exception e) {
//            logger.error("Failed to send invitation email to {}", to, e);
//        }
//
//    }
//}
