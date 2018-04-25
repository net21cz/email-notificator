package cz.net21.tools.emn;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.log4j.Log4j2;

@Log4j2
class SendEmail {

    private final Session session;

    private final String fromName;
    private final String fromEmail;

    public SendEmail(String host, String username, String password,
                     String fromName, String fromEmail) {
        Properties props = System.getProperties();

        props.setProperty("mail.smtp.host", host);

        Authenticator auth = null;
        if (username != null && !username.isEmpty()) {
            props.setProperty("mail.smtp.auth", "true");
            auth = new SMTPAuthenticator(username, password);
        }
        this.session = Session.getDefaultInstance(props, auth);

        this.fromName = fromName;
        this.fromEmail = fromEmail;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {

        private final String username;
        private final String password;

        public SMTPAuthenticator(final String username, final String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(String.format("%s <%s>", fromName, fromEmail)));
            message.setFrom();
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);

            log.debug(() -> String.format("Sent mail: %s to %s", subject, to));

        } catch (MessagingException e) {
            log.error("Sending an e-mail failed", e);
        }
    }
}
