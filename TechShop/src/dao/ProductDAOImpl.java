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
    private static List<Integer> productIDsInOrders = new ArrayList<>();
    private InventoryDAO inventoryDAO = new InventoryDAOImpl(); // Use interface type
    private Connection connection;

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
    public void removeProduct(int productID) throws ProductNotFoundException, ProductInUseException {
        if (productIDsInOrders.contains(productID)) {
            throw new ProductInUseException("Cannot remove product. It is associated with an existing order.");
        }

        Connection connection = null;
        PreparedStatement checkStmt = null;
        PreparedStatement deleteStmt = null;
        ResultSet rs = null;

        try {
            connection = DBConnUtil.getDbConnection();

           
            String checkQuery = "SELECT * FROM Products WHERE ProductID = ?";
            checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, productID);
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new ProductNotFoundException("Product with ID " + productID + " not found in database.");
            }

            // If exists, delete it
            String deleteQuery = "DELETE FROM Products WHERE ProductID = ?";
            deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, productID);

            int rows = deleteStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Product deleted from database.");
            } else {
                System.out.println("❌ Product could not be deleted.");
            }

            // Remove from inventory (if needed)
            inventoryDAO.removeFromInventory(productID, 1);

        } catch (SQLException e) {
            System.out.println("Error while deleting product from DB: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (deleteStmt != null) deleteStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public void markProductAsOrdered(int productID) {
        if (!productIDsInOrders.contains(productID)) {
            productIDsInOrders.add(productID);
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

    public void updateInventoryOnOrder(int productID, int quantityOrdered) {
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
