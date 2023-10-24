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

    @Value("${spring.mail.receiver}")
    private String receiverUserName;

    @Value("${spring.mail.subject}")
    private String messageSubject;

    public void sendSimpleEmail(String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderUserName);
        message.setTo(receiverUserName);
        message.setText(body);
        message.setSubject(messageSubject);
        mailSender.send(message);
    }
}