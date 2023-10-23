package art.pricetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderUserName;

    private final String messageSubject = "Price changed";

    private EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String toEmail, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderUserName);
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(messageSubject);
        mailSender.send(message);
    }
}