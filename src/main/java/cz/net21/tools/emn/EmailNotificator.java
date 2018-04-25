package cz.net21.tools.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EmailNotificator {

    public static void main(String[] args) {
        if (args.length < 3) {
            printHelp();
            System.exit(-1);
        }

        try {
            SendEmail sendEmail = buildSendMail(args[0]);
            String sql = FileUtils.readFileToString(new File(args[1]), Charset.defaultCharset());
            String message = FileUtils.readFileToString(new File(args[2]), Charset.defaultCharset());

            new NotificationProcessor(sendEmail, sql, message).run();

        } catch (Exception e) {
            log.error("Exception in the application.", e);
            System.exit(1);
        }
    }

    private static SendEmail buildSendMail(String propertiesFilename) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(propertiesFilename));

        return new SendEmail(
                props.getProperty("host", "localhost"),
                props.getProperty("username"),
                props.getProperty("password"),
                props.getProperty("from.name", "noreply"),
                props.getProperty("from.email", "noreply@localhost")
        );
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("<sendmail.properties> <recepients.sql> <message.txt>");
    }
}
