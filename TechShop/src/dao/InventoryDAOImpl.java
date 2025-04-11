package dao;

import entity.Inventory;
import exception.*;
import util.DBConnUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class InventoryDAOImpl implements InventoryDAO {
    private static List<Inventory> inventoryList = new ArrayList<>();

    public void addInventory(Inventory inventory) {
        inventoryList.add(inventory);
    }

    public void removeFromInventory(int inventoryID, int quantity) {
        Inventory inv = findInventoryById(inventoryID);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        if (quantity > inv.getQuantityInStock())
            throw new InsufficientStockException("Not enough stock.");
        inv.setQuantityInStock(inv.getQuantityInStock() - quantity);
    }

    public void addToInventory(int inventoryID, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        Inventory inv = findInventoryById(inventoryID);
        inv.setQuantityInStock(inv.getQuantityInStock() + quantity);
    }

    public void updateStock(int inventoryID, int newQuantity) {
        Inventory inv = findInventoryById(inventoryID);
        inv.setQuantityInStock(newQuantity);
    }

    public void listLowStockProducts(int threshold) {
        System.out.println("Low Stock Products:");
        for (Inventory inv : inventoryList) {
            if (inv.getQuantityInStock() < threshold) {
                System.out.println(inv.getProduct().getProductName() + " - Qty: " + inv.getQuantityInStock());
            }
        }
    }

    public void listOutOfStockProducts() {
        System.out.println("Out of Stock Products:");
        for (Inventory inv : inventoryList) {
            if (inv.getQuantityInStock() == 0) {
                System.out.println(inv.getProduct().getProductName());
            }
        }
    }

    public void listAllProducts() {
        System.out.println("All Inventory Products:");
        for (Inventory inv : inventoryList) {
            System.out.println("Product: " + inv.getProduct().getProductName() +
                    " | Quantity: " + inv.getQuantityInStock());
        }
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }
    
    public static boolean isProductAvailable(int productID, int requiredQuantity) {
        Inventory inventory = null;
        try {
            inventory = new InventoryDAOImpl().findInventoryById(productID);
        } catch (RuntimeException e) {
            return false; 
        }

        return inventory.getQuantityInStock() >= requiredQuantity;
    }


    private static Inventory findInventoryById(int inventoryID) {
        for (Inventory inv : inventoryList) {
            if (inv.getInventoryID() == inventoryID) return inv;
        }
        throw new RuntimeException("Inventory not found.");
    }
    
    @Override
    
    public void addProductToInventory(int inventoryID, int productID, int quantity) throws IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getDbConnection();

            // Try update first
            String updateQuery = "UPDATE Inventory SET QuantityInStock = QuantityInStock + ? WHERE InventoryID = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, quantity);
            stmt.setInt(2, inventoryID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                // If no rows updated, insert new entry
                stmt.close();
                String insertQuery = "INSERT INTO Inventory (InventoryID, ProductID, QuantityInStock) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setInt(1, inventoryID);
                stmt.setInt(2, productID);
                stmt.setInt(3, quantity);
                int insertResult = stmt.executeUpdate();

                if (insertResult > 0) {
                    System.out.println("Inventory inserted successfully.");
                } else {
                    System.out.println("Failed to insert inventory.");
                }
            } else {
                System.out.println("Inventory updated successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Error while updating/inserting inventory: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}