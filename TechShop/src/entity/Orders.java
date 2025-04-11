package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Orders {
    private int orderID;
    private Customers customer;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;

    public Orders(int orderID, Customers customer, LocalDateTime orderDate, double totalAmount) {
        setOrderID(orderID);
        setCustomer(customer);
        setOrderDate(orderDate);
        setTotalAmount(totalAmount);
        setStatus("Processing");
    }
    private int customerID;
    private List<OrderDetails> orderDetails;
    public Orders(int customerID, List<OrderDetails> orderDetails) {
        this.setCustomerID(customerID);
        this.orderDetails = orderDetails;
    }
    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Orders(int customerID2, Object customer2, LocalDateTime orderDate2, double totalAmount2) {
		// TODO Auto-generated constructor stub
	}


	public Orders(Customers customer2, LocalDateTime now, double total, List<OrderDetails> orderDetailsList) {
		// TODO Auto-generated constructor stub
	}
	public Orders(Customers customer2, LocalDate now, int count, List<OrderDetails> orderDetailsList) {
		// TODO Auto-generated constructor stub
	}
	public double calculateTotal() {
        double total = 0.0;
        if (orderDetails != null) {
            for (OrderDetails od : orderDetails) {
                Products product = od.getProduct();
                if (product != null) {
                    total += product.getPrice() * od.getQuantity();
                }
            }
        }
        return total;
    }

	public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        if (orderID > 0) this.orderID = orderID;
        else throw new IllegalArgumentException("Order ID must be positive.");
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        if (customer != null) this.customer = customer;
        else throw new IllegalArgumentException("Customer cannot be null.");
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate2) {
        if (orderDate2 != null) this.orderDate = orderDate2;
        else throw new IllegalArgumentException("Order date cannot be null.");
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount >= 0) this.totalAmount = totalAmount;
        else throw new IllegalArgumentException("Total amount cannot be negative.");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) this.status = status;
        else throw new IllegalArgumentException("Status cannot be null or empty.");
    }

    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }
	public int getCustomerID() {
	
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
}
