package entity;

import java.time.LocalDateTime;

public class Payment {
    private int paymentID;
    private int orderID;
    private double amountPaid;
    private String paymentStatus; // e.g., "Pending", "Completed", "Failed"
    private LocalDateTime paymentDate;

    public Payment(int paymentID, int orderID, double amountPaid, String paymentStatus) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
        this.paymentDate = LocalDateTime.now();
    }

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}



}
