package sdams.model;

public class UserAccount extends User {

    public UserAccount(String userId, String fullName, String role, String email,
                       String phone, String passwordHash, String status) {
        super(userId, fullName, role, email, phone, passwordHash, status);
    }

    /** Parses one users.txt line. Returns null if the line is malformed. */
    public static UserAccount fromFileString(String line) {
        String[] f = line.split("\\|", -1);
        if (f.length != 7) return null;
        return new UserAccount(f[0], f[1], f[2], f[3], f[4], f[5], f[6]);
    }

    @Override
    public void openMenu() {
        throw new UnsupportedOperationException("UserAccount is a data holder, not a login role.");
    }
}