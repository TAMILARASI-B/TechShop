package dao;

import entity.Products;
import exception.*;
import util.DBConnUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProductDAOImpl implements ProductDAO {
    private static List<Products> productList = new ArrayList<>();
    private InventoryDAO inventoryDAO = new InventoryDAOImpl(); 
    private Connection connection;
    
    public List<Products> searchProductsByKeyword(String keyword) throws IOException {
        List<Products> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String sql = "SELECT * FROM Products WHERE ProductName LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Products product = new Products(
                    rs.getInt("ProductID"),
                    rs.getString("ProductName"),
                    rs.getString("Category"),
                    rs.getDouble("Price")
                    
                );
                result.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void addProduct(Products product) throws DuplicateEntryException, IOException {
        if (InventoryDAOImpl.isProductAvailable(product.getProductID(), 1)) {
            throw new DuplicateEntryException("Product with the same ID already exists.");
        }

        PreparedStatement stmt = null;
        try {
            connection = DBConnUtil.getDbConnection();
            String insertQuery = "INSERT INTO Products (ProductID, ProductName, Description, Price) VALUES (?, ?, ?, ?)";
            stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, product.getProductID());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product added successfully.");
                productList.add(product); // Maintain in-memory list
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding product: " + e.getMessage());
        } finally {
            closeResources(stmt, connection);
        }
    }

    @Override
    public void updateProduct(Products product) throws IOException {
        PreparedStatement stmt = null;
        try {
            connection = DBConnUtil.getDbConnection();
            String updateQuery = "UPDATE Products SET ProductName = ?, Description = ?, Price = ? WHERE ProductID = ?";
            stmt = connection.prepareStatement(updateQuery);
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getProductID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product updated successfully.");
            } else {
                System.out.println("No product found with the given ProductID.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating product: " + e.getMessage());
        } finally {
            closeResources(stmt, connection);
        }
    }
    @Override
    public void removeProduct(int productId, int quantity) throws IOException {
        InventoryDAO inventoryDAO = new InventoryDAOImpl();
        inventoryDAO.removeFromInventory(productId, quantity);

        String checkInventory = "SELECT * FROM inventory WHERE ProductID = ?";
        String deleteProduct = "DELETE FROM products WHERE ProductID = ?";

        try (Connection conn = DBConnUtil.getDbConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkInventory)) {

            checkStmt.setInt(1, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) { 
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteProduct)) {
                    deleteStmt.setInt(1, productId);
                    int rows = deleteStmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("✅ Product deleted from database (Product ID: " + productId + ")");
                    } else {
                        System.out.println("⚠️ Product not deleted (Product ID: " + productId + ")");
                    }
                }
            } else {
                System.out.println("ℹ️ Inventory still exists for Product ID: " + productId);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error in removeProduct: " + e.getMessage());
        }
    }

    @Override
    public void listAllProducts() {
        for (Products p : productList) {
            p.getProductDetails();
            System.out.println("-------------------------");
        }
    }

    public List<Products> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be null or empty.");
        }

        return productList.stream()
                .filter(p -> p.getProductName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public List<Products> searchProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price.");
        }

        return productList.stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .toList();
    }

    public List<Products> getAllProducts() {
        return productList;
    }

    public void updateInventoryOnOrder(int productID, int quantityOrdered) throws IOException {
        try {
            inventoryDAO.removeFromInventory(productID, quantityOrdered);
        } catch (InsufficientStockException e) {
            System.out.println("[ERROR] Order failed due to insufficient stock: " + e.getMessage());
        }
    }

    private void closeResources(PreparedStatement stmt, Connection conn) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
    public Products getProductById(int productID) throws IOException {
        Products product = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnUtil.getDbConnection();
            String query = "SELECT * FROM Products WHERE ProductID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                product = new Products(
                    rs.getInt("ProductID"),
                    rs.getString("ProductName"),
                    rs.getString("Description"),
                    rs.getDouble("Price")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product by ID: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing DB resources: " + e.getMessage());
            }
        }
        return product;
    }
  
	

	
}