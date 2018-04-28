package cz.net21.tools.emn;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
class NotificationProcessor {

    private final Sendmail sendmail;

    private final Connection conn;
    private final String sql;

    private final String subject;
    private final String message;

    public void run() {
        Iterator<Map<String, String>> iterator = new SqlIterator(conn, sql);

        while (iterator.hasNext()) {
            Map<String, String> record = iterator.next();

            if (!record.containsKey("EMAIL")) {
                throw new IllegalStateException("No key 'EMAIL'! " + record);
            }

            String msg = resolveMessage(record, message);

            sendmail.send(record.get("EMAIL"), subject, msg);
        }
    }

    private String resolveMessage(Map<String, String> placeholderReplacements, String message) {
        for (Map.Entry<String, String> entry : placeholderReplacements.entrySet()) {

            String placeholder = String.format("${%s}", entry.getKey());
            message = message.replace(placeholder, entry.getValue());
        }
        return message;
    }
}
