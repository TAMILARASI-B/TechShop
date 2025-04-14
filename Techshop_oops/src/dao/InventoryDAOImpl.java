package dao;

import entity.*;
import util.DBConnUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InventoryDAOImpl implements InventoryDAO {
    private static List<Inventory> inventoryList = new ArrayList<>();

    public void addInventory(Inventory inventory) {
        inventoryList.add(inventory);
    }
    
    @Override
    public void removeFromInventory(int productId, int quantity) throws IOException {
        String selectSql = "SELECT QuantityInStock FROM inventory WHERE ProductID = ?";
        String updateSql = "UPDATE inventory SET QuantityInStock = ? WHERE ProductID = ?";
        String deleteSql = "DELETE FROM inventory WHERE ProductID= ?";

        try (Connection conn = DBConnUtil.getDbConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, productId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("QuantityInStock");
                System.out.println("🔍 Current stock: " + currentStock);

                int newStock = currentStock - quantity;
                if (newStock <= 0) {
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, productId);
                        int rows = deleteStmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("✅ Inventory deleted for Product ID: " + productId);
                        } else {
                            System.out.println("⚠️ Failed to delete inventory (Product ID: " + productId + ")");
                        }
                    }
                } else {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newStock);
                        updateStmt.setInt(2, productId);
                        int rows = updateStmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("✅ Inventory updated for Product ID: " + productId + ", new quantity: " + newStock);
                        } else {
                            System.out.println("⚠️ Failed to update inventory (Product ID: " + productId + ")");
                        }
                    }
                }
            } else {
                System.out.println("⚠️ No inventory found for Product ID: " + productId);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error in removeFromInventory: " + e.getMessage());
        }
    }




    public void addToInventory(int inventoryID, int quantity) throws IOException {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        Inventory inv = findInventoryById(inventoryID);
        inv.setQuantityInStock(inv.getQuantityInStock() + quantity);
    }

    public void updateStock(int inventoryID, int newQuantity) throws IOException {
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
    @Override
    public List<Inventory> getInventoryList() throws SQLException, IOException {
        List<Inventory> inventoryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String query = "SELECT * FROM Inventory";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int inventoryID = rs.getInt("InventoryID");
                int productID = rs.getInt("ProductID");
                int quantityInStock = rs.getInt("QuantityInStock");

               
                System.out.println("Retrieved QuantityInStock for ProductID " + productID + ": " + quantityInStock);

                
                if (quantityInStock < 0) {
                    System.out.println("Invalid QuantityInStock for ProductID " + productID + ". Setting it to 0.");
                    quantityInStock = 0;
                }

                ProductDAO productDAO = new ProductDAOImpl();
                Products product = productDAO.getProductById(productID);

                Inventory inventory = new Inventory(inventoryID, product, quantityInStock);
                inventoryList.add(inventory);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving inventory list: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return inventoryList;
    }

    
    public static boolean isProductAvailable(int productID, int requiredQuantity) throws IOException {
        Inventory inventory = null;
        try {
        	InventoryDAO inventoryDAO = new InventoryDAOImpl();
            inventory = inventoryDAO.findInventoryById(productID);

        } catch (RuntimeException e) {
            return false; 
        }

        return inventory != null && inventory.getQuantityInStock() >= requiredQuantity;
    }


    public  Inventory findInventoryById(int inventoryID)throws IOException {
        for (Inventory inv : inventoryList) {
            if (inv.getInventoryID() == inventoryID) return inv;
        }
        throw new RuntimeException("Inventory not found.");
    }
    
    @Override
    public boolean addProductToInventory(int inventoryID, int productID, int quantity) throws IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getDbConnection();

           
            String updateQuery = "UPDATE Inventory SET QuantityInStock = QuantityInStock + ? WHERE InventoryID = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, quantity);
            stmt.setInt(2, inventoryID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
              
                stmt.close(); 
                String insertQuery = "INSERT INTO Inventory (InventoryID, ProductID, QuantityInStock) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setInt(1, inventoryID);
                stmt.setInt(2, productID);
                stmt.setInt(3, quantity);

                int insertResult = stmt.executeUpdate();
                if (insertResult > 0) {
                    System.out.println("✅ Inventory inserted successfully.");
                    return true;
                } else {
                    System.out.println("❌ Failed to insert inventory.");
                    return false;
                }
            } else {
                System.out.println("✅ Inventory updated successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error while updating/inserting inventory: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("⚠️ Error closing resources: " + e.getMessage());
            }
        }
    }

    public int getProductStock(int productID) throws SQLException, IOException {
        int availableStock = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDbConnection();  
            String query = "SELECT QuantityInStock FROM Inventory WHERE ProductID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, productID);

            rs = stmt.executeQuery();

            if (rs.next()) {
                availableStock = rs.getInt("QuantityInStock");
            }
        } catch (SQLException |IOException e) {
            System.out.println("Error fetching product stock: " + e.getMessage());
            throw e;  
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return availableStock;
    }
    @Override
    public boolean removeProductFromInventory(int inventoryID, int productID, int quantity) throws IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getDbConnection();

            String checkQuery = "SELECT QuantityInStock FROM Inventory WHERE InventoryID = ? AND ProductID = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, inventoryID);
            stmt.setInt(2, productID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("QuantityInStock");

                if (currentStock >= quantity) {
                    stmt.close();

                    String updateQuery = "UPDATE Inventory SET QuantityInStock = QuantityInStock - ? WHERE InventoryID = ? AND ProductID = ?";
                    stmt = conn.prepareStatement(updateQuery);
                    stmt.setInt(1, quantity);
                    stmt.setInt(2, inventoryID);
                    stmt.setInt(3, productID);

                    int updated = stmt.executeUpdate();

                    if (updated > 0) {
                        System.out.println("✅ Inventory updated: Product removed.");
                        return true;
                    }
                } else {
                    System.out.println("❌ Not enough stock to remove.");
                }
            } else {
                System.out.println("❌ Inventory record not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error during inventory removal: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("⚠️ Error closing resources: " + e.getMessage());
            }
        }

        return false;
    }


}