package sdams.ui;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sdams.model.Payment;
import sdams.service.PaymentService;

/** Payment Records: view and search/filter past payments (read-only). */
public class PaymentRecordsPanel extends JPanel {
    private static final String[] COLUMNS = {"Payment ID", "Booking ID", "Student ID", "Student Name",
            "Amount", "Date", "Method", "Status"};

    private final PaymentService service = new PaymentService();
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> methodFilter =
            new JComboBox<>(new String[]{"ALL", "CARD", "CASH", "ONLINE"});
    private final JComboBox<String> statusFilter =
            new JComboBox<>(new String[]{"ALL", "SUCCESS", "PENDING", "REFUNDED"});
    private final JLabel summary = new JLabel(" ");

    public PaymentRecordsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search (payment / booking / student ID or name):"));
        top.add(searchField);
        top.add(new JLabel("Method:"));
        top.add(methodFilter);
        top.add(new JLabel("Status:"));
        top.add(statusFilter);
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");
        JButton refreshBtn = new JButton("Refresh");
        top.add(searchBtn);
        top.add(resetBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        summary.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(summary, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> refresh());
        refreshBtn.addActionListener(e -> refresh());
        searchField.addActionListener(e -> refresh());
        methodFilter.addActionListener(e -> refresh());
        statusFilter.addActionListener(e -> refresh());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            methodFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            refresh();
        });

        refresh();
    }

    public void refresh() {
        try {
            List<Payment> list = service.search(searchField.getText(),
                    (String) methodFilter.getSelectedItem(), (String) statusFilter.getSelectedItem());
            Map<String, String> names = service.studentNames();
            model.setRowCount(0);
            BigDecimal successTotal = BigDecimal.ZERO;
            for (Payment p : list) {
                model.addRow(new Object[]{p.getPaymentId(), p.getBookingId(), p.getStudentId(),
                        names.getOrDefault(p.getStudentId(), "(unknown)"),
                        p.getAmount().setScale(2), p.getPaymentDate(),
                        p.getPaymentMethod(), p.getPaymentStatus()});
                if ("SUCCESS".equals(p.getPaymentStatus())) successTotal = successTotal.add(p.getAmount());
            }
            summary.setText("Records shown: " + list.size()
                    + "   |   Total of successful payments shown: " + successTotal.setScale(2));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not read payments.txt: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}