package sdams.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sdams.model.Payment;
import sdams.model.UserAccount;
import sdams.util.FileHandler;

/** Read and search for payments.txt (Administrator view only, no editing). */
public class PaymentService {
    private static final String FILE = "payments.txt";

    public List<Payment> loadAll() throws IOException {
        List<Payment> list = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            Payment p = Payment.fromFileString(line);
            if (p != null) list.add(p);
        }
        return list;
    }

    /** Looks up student names so records can be searched by name as well as ID. */
    public Map<String, String> studentNames() throws IOException {
        Map<String, String> names = new HashMap<>();
        for (UserAccount u : new UserService().loadAll()) names.put(u.getUserId(), u.getFullName());
        return names;
    }

    /**
     * keyword matches payment ID, booking ID, student ID or student name.
     * method / status: "ALL" or an exact value such as CARD or SUCCESS.
     */
    public List<Payment> search(String keyword, String method, String status) throws IOException {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        Map<String, String> names = studentNames();
        List<Payment> result = new ArrayList<>();
        for (Payment p : loadAll()) {
            boolean methodOk = method == null || method.equals("ALL") || p.getPaymentMethod().equals(method);
            boolean statusOk = status == null || status.equals("ALL") || p.getPaymentStatus().equals(status);
            String name = names.getOrDefault(p.getStudentId(), "").toLowerCase();
            boolean textOk = k.isEmpty()
                    || p.getPaymentId().toLowerCase().contains(k)
                    || p.getBookingId().toLowerCase().contains(k)
                    || p.getStudentId().toLowerCase().contains(k)
                    || name.contains(k);
            if (methodOk && statusOk && textOk) result.add(p);
        }
        return result;
    }
}