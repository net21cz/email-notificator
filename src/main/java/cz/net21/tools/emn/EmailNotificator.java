package cz.net21.tools.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
            Properties settings = readSettings(args[0]);

            Sendmail sendmail = buildSendMail(settings);
            String sql = FileUtils.readFileToString(new File(args[1]), Charset.defaultCharset());
            String message = FileUtils.readFileToString(new File(args[2]), Charset.defaultCharset());
            String subject = settings.getProperty("subject");

            try (Connection conn = buildConnection(settings)) {
                new NotificationProcessor(sendmail, conn, sql, subject, message).run();
            }

        } catch (Exception e) {
            log.error("Exception in the application.", e);
            System.exit(1);
        }
    }

    private static Properties readSettings(String settingsFilename) throws IOException {
        Properties props = new Properties();
        try (InputStream properties = new FileInputStream(settingsFilename)) {
            props.load(properties);
            return props;
        }
    }

    private static Sendmail buildSendMail(Properties settings) {
        return new Sendmail(
                settings.getProperty("sedmail.host", "localhost"),
                settings.getProperty("sedmail.username"),
                settings.getProperty("sedmail.password"),
                settings.getProperty("from", "noreply@localhost")
        );
    }

    private static Connection buildConnection(Properties settings) throws SQLException {
        return DriverManager.getConnection(
                settings.getProperty("db.connectionString"),
                settings.getProperty("db.username"),
                settings.getProperty("db.password")
        );
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("< settings.properties> <recepients.sql> <message.txt>");
    }
}
