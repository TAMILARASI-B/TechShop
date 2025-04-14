package entity;

import exception.*;

public class OrderDetails {
    private int OrderDetailID;
    private Orders Order;
    private Products Product;
    private int Quantity;
    private double Discount;
 

    public OrderDetails(int orderDetailID, Orders order, Products product, int quantity) {
        setOrderDetailID(orderDetailID);
        setOrder(order);
        setProduct(product);
        setQuantity(quantity);
        this.Discount = 0.0;
    }
    public OrderDetails(int orderDetailID, Products product, int quantity, double price) {
        this.OrderDetailID = orderDetailID;
        this.Product = product;
        this.Quantity = quantity;
        this.Discount = price;
    }
    public OrderDetails(Products product, int quantity) {
        this.Product = product;
        this.Quantity = quantity;
    }

	public OrderDetails(int orderDetailID2, int productID, int quantity2, int quantity3) {
		// TODO Auto-generated constructor stub
	}
	public int getOrderDetailID() {
        return OrderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        if (orderDetailID > 0) this.OrderDetailID = orderDetailID;
        else throw new IllegalArgumentException("Order Detail ID must be positive.");
    }

    public Orders getOrder() {
        return Order;
    }

    public void setOrder(Orders order) {
        if (order != null) this.Order = order;
        else throw new IllegalArgumentException("Order cannot be null.");
    }

    public Products getProduct() {
        return Product;
    }

    public void setProduct(Products product) {
        if (product != null) {
            this.Product = product;
        } else {
            throw new IncompleteOrderDetailException("Order detail must include a valid product.");
        }
    }


    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) this.Quantity = quantity;
        else throw new IllegalArgumentException("Quantity must be positive.");
    }

    public double getDiscount() {
        return Discount;
    }

    public void AddDiscount(double discount) {
        if (discount >= 0 && discount <= 100) this.Discount = discount;
        else throw new IllegalArgumentException("Discount must be between 0 and 100.");
    }

    public double CalculateSubtotal() {
        double subtotal = Quantity * Product.getPrice();
        subtotal -= subtotal * (Discount / 100.0);
        return subtotal;
    }

    public void GetOrderDetailInfo() {
        System.out.println("Order Detail ID : " + OrderDetailID);
        System.out.println("Product         : " + Product.getProductName());
        System.out.println("Quantity        : " + Quantity);
        System.out.println("Price Each      : $" + Product.getPrice());
        System.out.println("Discount        : " + Discount + "%");
        System.out.println("Subtotal        : $" + CalculateSubtotal());
    }
    @Override
    public String toString() {
        return "OrderDetailID: " + OrderDetailID + ", Product: " + Product.getProductName() + ", Quantity: " + Quantity;
    }


    public void UpdateQuantity(int quantity) {
        setQuantity(quantity);
    }
    public int getProductId() {
        return Product != null ? Product.getProductID() : -1;
    }
}