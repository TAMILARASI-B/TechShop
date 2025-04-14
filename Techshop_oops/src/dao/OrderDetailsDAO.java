package dao;

import entity.OrderDetails;
import exception.InsufficientStockException;

import java.io.IOException;
import java.util.List;

public interface OrderDetailsDAO {
    void addOrderDetail(OrderDetails orderDetail) throws InsufficientStockException, IOException;
    void updateOrderDetailQuantity(int orderDetailID, int newQuantity) throws InsufficientStockException, IOException;
    void removeOrderDetail(int orderDetailID);
    List<OrderDetails> getAllOrderDetails();
    OrderDetails getOrderDetailById(int orderDetailID);
}