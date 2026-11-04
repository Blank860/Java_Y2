package sdams;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import sdams.model.Administrator;
import sdams.util.FileHandler;

public class AdminLauncher {
    public static void main(String[] args) {
        try {
            for (String line : FileHandler.readLines("users.txt")) {
                Administrator admin = Administrator.fromFileString(line);
                if (admin != null && admin.getUserId().equals("ADM001")) {
                    SwingUtilities.invokeLater(admin::openMenu);
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "ADM001 not found in data/users.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not read data/users.txt: " + e.getMessage());
        }
    }
}