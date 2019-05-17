package edu.fluffytiger.tea.service;

import edu.fluffytiger.tea.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final HtmlMailBuilder htmlBuilder;
    private final JavaMailSender mailSender;

    @Value("email.title.prefix")
    private String prefix;

    public EmailService(HtmlMailBuilder htmlBuilder, JavaMailSender mailSender) {
        this.htmlBuilder = htmlBuilder;
        this.mailSender = mailSender;
    }

    public void sendEvent(Event event) {
        MimeMessagePreparator preparator;

        if (!event.isCustom()) {
            preparator = message -> {
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(event.getEmails().toArray(new String[0]));
                helper.setSubject(prefix + event.getName());
                helper.setText(htmlBuilder.build(event), true);
            };
        } else {
            preparator = message -> {
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(event.getEmails().toArray(new String[0]));
                helper.setSubject(prefix + event.getName());
                helper.setText(event.getDescription(), true);
            };
        }

        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}