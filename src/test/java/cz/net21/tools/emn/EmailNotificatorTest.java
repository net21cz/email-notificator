package cz.net21.tools.emn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailNotificatorTest {

    private static final String dbConnectionString = "jdbc:h2:mem:";

    private static final int DATA_COUNT = SqlIterator.LIMIT * 2 + SqlIterator.LIMIT / 2;
    private static final int DATA_COUNT2 = 2;

    private Connection conn;

    @Before
    public void setup() throws SQLException {
        initMocks(EmailNotificatorTest.class);

        conn = DriverManager.getConnection(dbConnectionString);
        fillData();
    }

    @After
    public void tearDown() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE test");
            st.executeUpdate("DROP TABLE test2");
        }
    }

    public void fillData() throws SQLException {
        try (Statement st = conn.createStatement()) {

            st.execute("CREATE TABLE test(id INTEGER, email VARCHAR(50))");

            for (int i = 1; i <= DATA_COUNT; i++) {
                st.execute(String.format("INSERT INTO test VALUES (%d, '%s')", i, buildEmail(i)));
            }

            st.execute("CREATE TABLE test2(id INTEGER, email VARCHAR(50))");

            for (int i = 1; i <= DATA_COUNT2; i++) {
                st.execute(String.format("INSERT INTO test2 VALUES (%d, '%s')", i, buildEmail(i)));
            }
        }
    }

    @Test
    public void sqlIteratorTest() {
        SqlIterator iterator = new SqlIterator(conn, "SELECT id, email FROM test");

        checkIterator(iterator, DATA_COUNT);
    }

    @Test
    public void sqlIterator2Test() {
        SqlIterator iterator = new SqlIterator(conn, "SELECT id, email FROM test2");

        checkIterator(iterator, DATA_COUNT2);
    }

    private void checkIterator(SqlIterator iterator, int expectedCount) {
        assertThat(iterator, not(nullValue()));

        assertThat(iterator.hasNext(), is(true));

        int count = 0;
        while (iterator.hasNext()) {
            count++;
            Map<String, String> rec = iterator.next();

            assertThat(rec.containsKey("ID"), is(true));
            assertThat(rec.containsKey("EMAIL"), is(true));

            assertThat(rec.get("ID"), is(String.valueOf(count)));
            assertThat(rec.get("EMAIL"), is(buildEmail(count)));
        }

        assertThat(count, is(expectedCount));
    }

    @Test
    public void processorTest() {
        Sendmail sendmail = mock(Sendmail.class);

        NotificationProcessor processor = new NotificationProcessor(
                sendmail,
                conn,
                "SELECT id, email FROM test2",
                "Subject",
                "Message ${ID} ${EMAIL}"
        );

        processor.run();

        for (int i = 1; i <= DATA_COUNT2; i++) {
            verify(sendmail).send(
                    buildEmail(i),
                    "Subject",
                    String.format("Message %s %s", i, buildEmail(i))
            );
        }
    }

    private String buildEmail(int i) {
        return String.format("%d.%s", i, "test@example.com");
    }
}
