package cz.net21.tools.emn;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
class NotificationProcessor {

    private final SendEmail sendEmail;
    private final String sql;
    private final String message;

    public void run() {

    }
}
