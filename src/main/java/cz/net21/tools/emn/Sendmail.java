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
class Sendmail {

    private final Session session;

    private final String from;

    public Sendmail(String host, String username, String password, String from) {
        Properties props = System.getProperties();

        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.mime.charset", "UTF-8");

        Authenticator auth = null;
        if (username != null && !username.isEmpty()) {
            props.setProperty("mail.smtp.auth", "true");
            auth = new SMTPAuthenticator(username, password);
        }
        this.session = Session.getDefaultInstance(props, auth);

        this.from = from;
    }

    private class SMTPAuthenticator extends Authenticator {

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

    // TODO https://support.google.com/mail/answer/81126
    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(from));
            message.setFrom(new InternetAddress(from));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text, "UTF-8");
            message.addHeader("X-Mailer", "NET21 Mailer");

            Transport.send(message);

            log.info(() -> String.format("Sent mail: %s to %s", subject, to));

        } catch (MessagingException e) {
            log.error("Sending an e-mail failed", e);
        }
    }
}
