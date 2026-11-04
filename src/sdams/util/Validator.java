package sdams.util;

import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{9,13}$");

    private Validator() { }

    public static String requireText(String value, String field, int maxLen) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException(field + " cannot be empty.");
        if (v.length() > maxLen) throw new IllegalArgumentException(field + " must be at most " + maxLen + " characters.");
        if (v.contains("|")) throw new IllegalArgumentException(field + " cannot contain the '|' character.");
        if (v.contains("\n") || v.contains("\r")) throw new IllegalArgumentException(field + " cannot contain line breaks.");
        return v;
    }

    public static String email(String value) {
        String v = requireText(value, "Email", 60);
        if (!EMAIL.matcher(v).matches()) throw new IllegalArgumentException("Email format is invalid (example: name@example.com).");
        return v;
    }

    public static String phone(String value) {
        String v = requireText(value, "Phone", 15);
        if (!PHONE.matcher(v).matches()) throw new IllegalArgumentException("Phone must be 9 to 13 digits (optional leading +).");
        return v;
    }

    public static String password(String value) {
        if (value == null || value.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (value.length() > 30) throw new IllegalArgumentException("Password must be at most 30 characters.");
        if (value.contains("|") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("Password cannot contain '|' or line breaks.");
        }
        return value;
    }
}
