package sdams.ui;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sdams.model.UserAccount;
import sdams.service.UserService;
import sdams.util.PasswordUtil;
import sdams.util.Validator;

/** Reusable add / view / search / update / delete screen for a group of roles. */
public class UserManagementPanel extends JPanel {
    private static final String[] COLUMNS = {"ID", "Full Name", "Role", "Email", "Phone", "Status"};
    private static final String[] STATUSES = {"ACTIVE", "INACTIVE"};

    private final String entityName;
    private final List<String> roles;
    private final UserService service = new UserService();
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(20);

    public UserManagementPanel(String entityName, List<String> roles) {
        this.entityName = entityName;
        this.roles = roles;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search (ID / name / email / phone / role / status):"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");
        top.add(searchBtn);
        top.add(resetBtn);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Add " + entityName);
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        bottom.add(addBtn);
        bottom.add(updateBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> refresh());
        searchField.addActionListener(e -> refresh());
        resetBtn.addActionListener(e -> { searchField.setText(""); refresh(); });
        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());

        refresh();
    }

    public void refresh() {
        try {
            List<UserAccount> list = service.search(searchField.getText(), roles);
            model.setRowCount(0);
            for (UserAccount u : list) {
                model.addRow(new Object[]{u.getUserId(), u.getFullName(), u.getRole(),
                        u.getEmail(), u.getPhone(), u.getStatus()});
            }
        } catch (IOException ex) {
            error("Could not read users.txt: " + ex.getMessage());
        }
    }

    private void onAdd() {
        if (showFormAndSave(null)) {
            refresh();
            JOptionPane.showMessageDialog(this, entityName + " added.");
        }
    }

    private void onUpdate() {
        UserAccount selected = getSelected();
        if (selected == null) return;
        if (showFormAndSave(selected)) {
            refresh();
            JOptionPane.showMessageDialog(this, entityName + " updated.");
        }
    }

    private void onDelete() {
        UserAccount selected = getSelected();
        if (selected == null) return;
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete " + selected.getUserId() + " - " + selected.getFullName() + "?\nThis cannot be undone.",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.delete(selected.getUserId(), roles);
            refresh();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot delete", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            error("Could not delete: " + ex.getMessage());
        }
    }

    private UserAccount getSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "No selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String id = (String) model.getValueAt(table.convertRowIndexToModel(viewRow), 0);
        try {
            return service.findById(id);
        } catch (IOException ex) {
            error("Could not read users.txt: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Shows the add (existing == null) or update form and saves it.
     * Keeps the form open until the input is valid. Returns true if saved.
     */
    private boolean showFormAndSave(UserAccount existing) {
        boolean isNew = existing == null;

        JComboBox<String> roleBox = new JComboBox<>(roles.toArray(new String[0]));
        if (!isNew) {
            roleBox.setSelectedItem(existing.getRole());
            roleBox.setEnabled(false);   // role decides the ID prefix, so it cannot change
        }
        JTextField name = new JTextField(isNew ? "" : existing.getFullName(), 20);
        JTextField email = new JTextField(isNew ? "" : existing.getEmail(), 20);
        JTextField phone = new JTextField(isNew ? "" : existing.getPhone(), 20);
        JPasswordField password = new JPasswordField(20);
        JComboBox<String> status = new JComboBox<>(STATUSES);
        if (!isNew) status.setSelectedItem(existing.getStatus());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("ID:"));        form.add(new JLabel(isNew ? "(auto-generated)" : existing.getUserId()));
        form.add(new JLabel("Role:"));      form.add(roleBox);
        form.add(new JLabel("Full name:")); form.add(name);
        form.add(new JLabel("Email:"));     form.add(email);
        form.add(new JLabel("Phone:"));     form.add(phone);
        form.add(new JLabel(isNew ? "Password:" : "New password (blank = keep):")); form.add(password);
        form.add(new JLabel("Status:"));    form.add(status);

        while (true) {
            int r = JOptionPane.showConfirmDialog(this, form,
                    (isNew ? "Add " : "Update ") + entityName,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return false;
            try {
                String role = (String) roleBox.getSelectedItem();
                String typedPassword = new String(password.getPassword());
                String hash = (isNew || !typedPassword.isEmpty())
                        ? PasswordUtil.hash(Validator.password(typedPassword))
                        : existing.getPasswordHash();
                String id = isNew ? service.nextId(role) : existing.getUserId();

                UserAccount account = new UserAccount(id,
                        Validator.requireText(name.getText(), "Full name", 50),
                        role,
                        Validator.email(email.getText()),
                        Validator.phone(phone.getText()),
                        hash,
                        (String) status.getSelectedItem());

                if (isNew) service.add(account); else service.update(account);
                return true;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                error("Could not save: " + ex.getMessage());
                return false;
            }
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}