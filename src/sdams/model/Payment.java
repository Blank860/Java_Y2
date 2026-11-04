package sdams.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * payments.txt:
 * paymentId|bookingId|studentId|amount|paymentDate|paymentMethod|paymentStatus
 */
public class Payment {
    private final String paymentId;
    private final String bookingId;
    private final String studentId;
    private final BigDecimal amount;
    private final LocalDate paymentDate;
    private final String paymentMethod;
    private final String paymentStatus;

    public Payment(String paymentId, String bookingId, String studentId, BigDecimal amount,
                   LocalDate paymentDate, String paymentMethod, String paymentStatus) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public String toFileString() {
        return String.join("|", paymentId, bookingId, studentId, amount.toPlainString(),
                paymentDate.toString(), paymentMethod, paymentStatus);
    }

    /** Returns null if the line is malformed (bad lines are skipped, not fatal). */
    public static Payment fromFileString(String line) {
        String[] f = line.split("\\|", -1);
        if (f.length != 7) return null;
        try {
            return new Payment(f[0], f[1], f[2], new BigDecimal(f[3]), LocalDate.parse(f[4]), f[5], f[6]);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public String getPaymentId() { return paymentId; }
    public String getBookingId() { return bookingId; }
    public String getStudentId() { return studentId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
}