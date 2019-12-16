package controlers;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberManager {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
    }

    private NumberManager() {
    }

    public static String formatNumber(Long aLong) {
        if (aLong < 1000) return Long.toString(aLong);

        Map.Entry<Long, String> entry = suffixes.floorEntry(aLong);
        Long divider = entry.getKey();
        String entryValue = entry.getValue();

        long truncated = aLong / (divider / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + entryValue : (truncated / 10) + entryValue;
    }
}
