package dao;

import entity.Payment;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private static List<Payment> paymentList = new ArrayList<>();

    @Override
    public void recordPayment(Payment payment) {
        paymentList.add(payment);
        System.out.println("Payment recorded for Order ID: " + payment.getOrderID());
    }

    @Override
    public void updatePaymentStatus(int paymentID, String newStatus) {
        for (Payment p : paymentList) {
            if (p.getPaymentID() == paymentID) {
                p.setPaymentStatus(newStatus);
                System.out.println("Payment status updated for Payment ID: " + paymentID);
                return;
            }
        }
        System.out.println("[ERROR] Payment ID not found.");
    }

    @Override
    public List<Payment> getPaymentsByOrderID(int orderID) {
        List<Payment> results = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getOrderID() == orderID) {
                results.add(p);
            }
        }
        return results;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentList;
    }
}
