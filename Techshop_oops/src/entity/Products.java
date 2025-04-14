package entity;

public class Products {
    private int ProductID;
    private String ProductName;
    private String Description;
    private double Price;
   

    public Products(int productID, String productName, String description, double price) {
        setProductID(productID);
        setProductName(productName);
        setDescription(description);
        setPrice(price);
    }


	public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        if (productID > 0) this.ProductID = productID;
        else throw new IllegalArgumentException("Product ID must be positive.");
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        if (productName != null && !productName.trim().isEmpty()) this.ProductName = productName;
        else throw new IllegalArgumentException("Product name cannot be null or empty.");
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        if (description != null) this.Description = description;
        else throw new IllegalArgumentException("Description cannot be null.");
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        if (price >= 0) this.Price = price;
        else throw new IllegalArgumentException("Price cannot be negative.");
    }

    public void updateProductInfo(String description, double price) {
        setDescription(description);
        setPrice(price);
    }

    public void getProductDetails() {
        System.out.println("Product ID   : " + ProductID);
        System.out.println("Name         : " + ProductName);
        System.out.println("Description  : " + Description);
        System.out.println("Price        : $" + Price);
    }
}