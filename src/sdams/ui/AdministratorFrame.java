package sdams.ui;

import java.awt.*;
import javax.swing.*;
import sdams.model.Administrator;

/** Main window shown to an Administrator after login (only this role's menu). */
public class AdministratorFrame extends JFrame {

    public AdministratorFrame(Administrator admin) {
        super("SDAMS - Administrator");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel welcome = new JLabel("Welcome, " + admin.getFullName() + " (" + admin.getUserId() + ")");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 15f));
        JButton logout = new JButton("Logout");
        header.add(welcome, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Staff", new StaffPanel());
        tabs.addTab("Manage Students", new StudentPanel());
        tabs.addTab("Payment Records", new PaymentRecordsPanel());
        add(tabs, BorderLayout.CENTER);

        // In the integrated system, Logout should return to the shared Login screen.
        logout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) dispose();
        });
    }

    private JPanel placeholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }
}