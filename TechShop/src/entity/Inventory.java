package entity;

import java.time.LocalDateTime;

public class Inventory {
    private int InventoryID;
    private Products Product;
    private int QuantityInStock;
    private LocalDateTime LastStockUpdate;

    public Inventory(int inventoryID, Products product, int quantityInStock) {
        setInventoryID(inventoryID);
        setProduct(product);
        setQuantityInStock(quantityInStock);
        this.LastStockUpdate = LocalDateTime.now();
    }

    public int getInventoryID() { return InventoryID; }
    public void setInventoryID(int inventoryID) {
        if (inventoryID > 0) this.InventoryID = inventoryID;
        else throw new IllegalArgumentException("Inventory ID must be positive.");
    }

    public Products getProduct() { return Product; }
    public void setProduct(Products product) {
        if (product != null) this.Product = product;
        else throw new IllegalArgumentException("Product cannot be null.");
    }

    public int getQuantityInStock() { return QuantityInStock; }
    public void setQuantityInStock(int quantityInStock) {
        if (quantityInStock >= 0) {
            this.QuantityInStock = quantityInStock;
            this.LastStockUpdate = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("Quantity must be non-negative.");
        }
    }

    public LocalDateTime getLastStockUpdate() { return LastStockUpdate; }
    public double getInventoryValue() { return Product.getPrice() * QuantityInStock; }
    public boolean isProductAvailable(int quantity) { return quantity <= QuantityInStock; }
}
