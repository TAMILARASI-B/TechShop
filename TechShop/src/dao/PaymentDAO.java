package dao;

import entity.Payment;
import java.util.List;

public interface PaymentDAO {
    void recordPayment(Payment payment);
    void updatePaymentStatus(int paymentID, String newStatus);
    List<Payment> getPaymentsByOrderID(int orderID);
    List<Payment> getAllPayments();
}
