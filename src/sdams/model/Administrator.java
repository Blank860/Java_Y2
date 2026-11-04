package sdams.model;

import javax.swing.SwingUtilities;
import sdams.ui.AdministratorFrame;

public class Administrator extends User {

    public Administrator(String userId, String fullName, String role, String email,
                         String phone, String passwordHash, String status) {
        super(userId, fullName, role, email, phone, passwordHash, status);
    }

    /** Parses one users.txt line. Returns null if the line is malformed. */
    public static Administrator fromFileString(String line) {
        String[] f = line.split("\\|", -1);
        if (f.length != 7) return null;
        return new Administrator(f[0], f[1], f[2], f[3], f[4], f[5], f[6]);
    }

    @Override
    public void openMenu() {
        SwingUtilities.invokeLater(() -> new AdministratorFrame(this).setVisible(true));
    }
}