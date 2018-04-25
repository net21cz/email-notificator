package cz.net21.tools.emn;

import java.util.Iterator;
import java.util.Map;

class RecipientsIterator implements Iterator<Map<String, String>> {

    private static final int LIMIT = 100;

    private final String sql;

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Map<String, String> next() {
        return null;
    }
}
