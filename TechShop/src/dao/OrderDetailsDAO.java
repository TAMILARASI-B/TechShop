package dao;

import entity.OrderDetails;
import exception.InsufficientStockException;
import java.util.List;

public interface OrderDetailsDAO {
    void addOrderDetail(OrderDetails orderDetail) throws InsufficientStockException;
    void updateOrderDetailQuantity(int orderDetailID, int newQuantity) throws InsufficientStockException;
    void removeOrderDetail(int orderDetailID);
    List<OrderDetails> getAllOrderDetails();
    OrderDetails getOrderDetailById(int orderDetailID);
}
