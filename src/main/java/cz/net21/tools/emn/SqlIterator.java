package cz.net21.tools.emn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class SqlIterator implements Iterator<Map<String, String>> {

    static final int LIMIT = 100;

    private final Connection conn;
    private final String sql;

    private int start = 0;

    private int index = 0;
    private Map<String, String> next;
    private List<Map<String, String>> results;

    SqlIterator(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;

        this.results = fetch();
        this.next = !results.isEmpty() ? results.get(0) : null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Map<String, String> next() {
        if (!hasNext()) {
            return null;
        }
        Map<String, String> res = next;
        index++;
        if (index == LIMIT) {
            results = fetch();
            index = 0;
            next = null;
        }
        if (!results.isEmpty()) {
            if (results.size() <= index) {
                index = 0;
                next = null;
            } else {
                next = results.get(index);
            }
        }
        return res;
    }

    private List<Map<String, String>> fetch() {
        List<Map<String, String>> results = new ArrayList<>();

        try (PreparedStatement st = prepareLimitedStatement(sql, start, LIMIT);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(buildRecord(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error by fetching customers", e);
        }

        start += LIMIT;

        return results;
    }

    private PreparedStatement prepareLimitedStatement(String sql, int start, int limit) throws SQLException {
        PreparedStatement st = conn.prepareStatement(sql + " LIMIT ?,?");
        st.setInt(1, start);
        st.setInt(2, limit);
        return st;
    }

    private Map<String, String> buildRecord(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        Map<String, String> rec = new HashMap<>(metaData.getColumnCount());

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            rec.put(metaData.getColumnName(i).toUpperCase(), rs.getString(i));
        }
        return rec;
    }
}
