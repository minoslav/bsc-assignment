package sk.jancar.bsc.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Currency {

    public static final String FORMAT_REGEX = "[A-Z][A-Z][A-Z]";
    private static final Map<String, Currency> instances = new ConcurrentHashMap<>();

    private final String code;

    public static Currency of(String code) {
        if (code == null) throw new IllegalArgumentException("Code cannot be null");
        if (!instances.containsKey(code)) {
            if (!Pattern.matches(FORMAT_REGEX, code)) throw new IllegalArgumentException("Code must be 3 capital letters");
            instances.computeIfAbsent(code, Currency::new); //atomic
        }
        return instances.get(code);
    }

    private Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        return code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
