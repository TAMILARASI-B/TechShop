package dao;

import entity.*;
import exception.*;
import util.DBConnUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*; 
import java.sql.Timestamp;

public class OrderDAOImpl implements OrderDAO {

    private static List<Orders> ordersList = new ArrayList<>();
    private static SortedMap<Integer, Integer> inventory = new TreeMap<>();
   

    @Override
    public void addOrder(Orders order) {
        ordersList.add(order);
    }

    @Override
    public void updateOrderStatus(int orderID, String newStatus) {
        for (Orders o : ordersList) {
            if (o.getOrderID() == orderID) {
                o.setStatus(newStatus);
                return;
            }
        }
        throw new RuntimeException("Order not found to update status.");
    }

    @Override
    public void removeCanceledOrder(int orderID) {
        Iterator<Orders> iterator = ordersList.iterator();
        while (iterator.hasNext()) {
            Orders o = iterator.next();
            if ("Cancelled".equalsIgnoreCase(o.getStatus()) && o.getOrderID() == orderID) {
                iterator.remove();
                return;
            }
        }
        throw new RuntimeException("Canceled order not found or already removed.");
    }

    @Override
    public List<Orders> getOrdersList() {
        return ordersList;
    }

    @Override
    public List<Orders> sortOrdersByDate(boolean ascending) {
        List<Orders> sorted = new ArrayList<>(ordersList);
        sorted.sort((o1, o2) -> ascending ? o1.getOrderDate().compareTo(o2.getOrderDate())
                                          : o2.getOrderDate().compareTo(o1.getOrderDate()));
        return sorted;
    }

    @Override
    public List<Orders> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Orders> filtered = new ArrayList<>();
        for (Orders order : ordersList) {
            if (!order.getOrderDate().isBefore(startDate) && !order.getOrderDate().isAfter(endDate)) {
                filtered.add(order);
            }
        }
        return filtered;
    }

    @Override
    public void processPayment(Orders order, double amount) {
        if (amount >= order.getTotalAmount()) {
            System.out.println("Payment of $" + amount + " processed successfully.");
            order.setStatus("Paid");
        } else {
            throw new PaymentFailedException("Insufficient payment. Please try again.");
        }
    }

    @Override
    public void cancelOrder(Orders order, String username, String password)
            throws AuthorizationException, AuthenticationException {
        if (authenticate(username, password)) {
            if (authorize("ORDER_CANCEL")) {
                order.setStatus("Cancelled");
                System.out.println("Order " + order.getOrderID() + " cancelled.");
            } else {
                throw new AuthorizationException("Access denied: Unauthorized to cancel orders.");
            }
        } else {
            throw new AuthenticationException("Invalid credentials for cancelling order.");
        }
    }


    private boolean authenticate(String username, String password) {
        return "admin".equals(username) && "password123".equals(password);
    }

    private boolean authorize(String permission) {
        return "ORDER_CANCEL".equals(permission);
    }

    @Override
    public SortedMap<Integer, Integer> getFullInventory() {
        return inventory;
    }

    @Override
    public void addToInventory(int productID, int quantity) {
        inventory.put(productID, inventory.getOrDefault(productID, 0) + quantity);
    }
   
    public void placeOrder(Orders order, List<OrderDetails> orderDetailsList) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement detailStmt = null;
        PreparedStatement inventoryStmt = null;

        try {
            conn = DBConnUtil.getDbConnection();
            conn.setAutoCommit(false);

           
            String insertOrderSQL = "INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, Status) VALUES (?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getCustomer().getCustomerID());
            orderStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.setDouble(3, 0); 
            orderStmt.setString(4, "Placed");

            int orderRowsAffected = orderStmt.executeUpdate();
            if (orderRowsAffected == 0) {
                throw new RuntimeException("Failed to insert order. No rows affected.");
            }

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new RuntimeException("Order ID generation failed.");
            }
            int generatedOrderID = rs.getInt(1);

            double total = 0;

           
            String insertDetailsSQL = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?)";
            detailStmt = conn.prepareStatement(insertDetailsSQL);

            String updateInventorySQL = "UPDATE Inventory SET QuantityInStock = QuantityInStock - ? WHERE ProductID = ?";
            inventoryStmt = conn.prepareStatement(updateInventorySQL);

            for (OrderDetails detail : orderDetailsList) {
                double price = fetchProductPrice(detail.getProductId(), conn);
                if (price < 0) {
                    throw new RuntimeException("Invalid price fetched for ProductID: " + detail.getProductId());
                }
                double subtotal = price * detail.getQuantity();
                total += subtotal;

                detailStmt.setInt(1, generatedOrderID);
                detailStmt.setInt(2, detail.getProductId());
                detailStmt.setInt(3, detail.getQuantity());
                detailStmt.setDouble(4, subtotal);
                detailStmt.addBatch();

                inventoryStmt.setInt(1, detail.getQuantity());
                inventoryStmt.setInt(2, detail.getProductId());
                inventoryStmt.addBatch();
            }

            detailStmt.executeBatch();
            inventoryStmt.executeBatch();

         
            String updateTotalSQL = "UPDATE Orders SET TotalAmount = ? WHERE OrderID = ?";
            try (PreparedStatement updateTotalStmt = conn.prepareStatement(updateTotalSQL)) {
                updateTotalStmt.setDouble(1, total);
                updateTotalStmt.setInt(2, generatedOrderID);
                updateTotalStmt.executeUpdate();
            }

            
            conn.commit();
            System.out.println("✅ Order placed successfully. Order ID: " + generatedOrderID + " | Total: $" + total);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    System.out.println("❌ Rollback failed: " + rollbackException.getMessage());
                }
            }
            System.out.println("❌ Error placing order: " + e.getMessage());
            throw new RuntimeException("Error placing order: " + e.getMessage(), e);
        } finally {
            try {
                if (orderStmt != null) orderStmt.close();
                if (detailStmt != null) detailStmt.close();
                if (inventoryStmt != null) inventoryStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Error closing resources: " + e.getMessage());
            }
        }
    }


    private double fetchProductPrice(int productID, Connection conn) throws SQLException {
        String query = "SELECT Price FROM Products WHERE ProductID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Price");
            } else {
                throw new RuntimeException("Product with ID " + productID + " not found.");
            }
        }
    }

  
    @Override
    public Orders getOrderById(int orderID) throws IOException {
        Orders order = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String query = "SELECT * FROM Orders WHERE OrderID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderID);

            rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp orderTimestamp = rs.getTimestamp("OrderDate");
                LocalDateTime orderDate = orderTimestamp.toLocalDateTime();
                double totalAmount = rs.getDouble("TotalAmount");
                String status = rs.getString("Status");

                // Use a constructor that doesn't require Customer
                order = new Orders(orderID, orderDate, totalAmount, status);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving order: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return order;
    }

   
    public void updateOrderStatus(Orders order) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String updateQuery = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, order.getStatus());
            stmt.setInt(2, order.getOrderID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Order not found to update status.");
            }

        } catch (SQLException e) {
            throw new SQLException("Error updating order status: " + e.getMessage());
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }


}