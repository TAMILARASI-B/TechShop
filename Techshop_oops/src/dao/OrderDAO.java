package dao;

import entity.OrderDetails;
import entity.Orders;
import exception.AuthenticationException;
import exception.AuthorizationException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;

public interface OrderDAO {
    void addOrder(Orders order);
    void updateOrderStatus(int orderID, String newStatus);
    void removeCanceledOrder(int orderID);
    List<Orders> getOrdersList();
    List<Orders> sortOrdersByDate(boolean ascending);
    List<Orders> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    void processPayment(Orders order, double amount);
    void cancelOrder(Orders order, String username, String password) throws AuthorizationException, AuthenticationException;
    SortedMap<Integer, Integer> getFullInventory();
    void addToInventory(int productID, int quantity);
	Orders getOrderById(int orderID) throws IOException, SQLException;
	void placeOrder(Orders order, List<OrderDetails> orderDetailsList) throws SQLException, IOException;
	void updateOrderStatus(Orders order) throws SQLException, IOException;
}