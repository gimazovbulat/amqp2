package ru.itis;

import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailService {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "lazyfucc69@gmail.com";
    private static final String PASSWORD = "aherip09";

    private static final String EMAIL_FROM = "lazyfucc69@gmail.com";

    private Session session;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MailService() {
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(props, null);
    }

    public void sendMailWithAttachment(String email, String path) {
        executorService.submit(() -> {
            Message msg = new MimeMessage(session);

            try {
                msg.setFrom(new InternetAddress(EMAIL_FROM));

                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(email, false));

                MimeBodyPart p2 = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(path);
                p2.setDataHandler(new DataHandler(fds));
                p2.setFileName(fds.getName());

                Multipart mp = new MimeMultipart();
                mp.addBodyPart(p2);

                msg.setContent(mp);

                SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

                // connect
                t.connect(SMTP_SERVER, USERNAME, PASSWORD);

                // send
                t.sendMessage(msg, msg.getAllRecipients());

                System.out.println("Response: " + t.getLastServerResponse());

                t.close();
            } catch (MessagingException e) {
                throw new IllegalStateException(e);
            }
        });

    }

    public void sendSimpleMail(String email, String msgToSend) {
        Message msg = new MimeMessage(session);
        executorService.submit(() -> {
            try {

                // from
                msg.setFrom(new InternetAddress(EMAIL_FROM));

                // to
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(email, false));

                // content
                msg.setText(msgToSend);

                // Get SMTPTransport
                SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

                // connect
                t.connect(SMTP_SERVER, USERNAME, PASSWORD);

                // send
                t.sendMessage(msg, msg.getAllRecipients());

                System.out.println("Response: " + t.getLastServerResponse());

                t.close();

            } catch (MessagingException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
