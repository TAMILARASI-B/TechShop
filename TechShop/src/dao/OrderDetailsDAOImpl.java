package dao;
import entity.*;
import exception.InsufficientStockException;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    private List<OrderDetails> orderDetailsList = new ArrayList<>();
    private InventoryDAOImpl inventoryDAO = new InventoryDAOImpl();

    @Override
    public void addOrderDetail(OrderDetails orderDetail) throws InsufficientStockException {
        int productId = orderDetail.getProduct().getProductID();
        int quantity = orderDetail.getQuantity();
        
        if (!InventoryDAOImpl.isProductAvailable(productId, quantity)) {
            throw new InsufficientStockException("Not enough stock for product ID: " + productId);
        }

        inventoryDAO.removeFromInventory(productId, quantity);
        orderDetailsList.add(orderDetail);
    }

    @Override
    public void updateOrderDetailQuantity(int orderDetailID, int newQuantity) throws InsufficientStockException {
        for (OrderDetails od : orderDetailsList) {
            if (od.getOrderDetailID() == orderDetailID) {
                int productId = od.getProduct().getProductID();
                int oldQuantity = od.getQuantity();

                if (newQuantity > oldQuantity) {
                    int diff = newQuantity - oldQuantity;
                    if (!InventoryDAOImpl.isProductAvailable(productId, diff)) {
                        throw new InsufficientStockException("Not enough stock to update quantity.");
                    }
                    inventoryDAO.removeFromInventory(productId, diff);
                } else if (newQuantity < oldQuantity) {
                    inventoryDAO.addToInventory(productId, oldQuantity - newQuantity);
                }

                od.setQuantity(newQuantity);
                return;
            }
        }
    }

    @Override
    public void removeOrderDetail(int orderDetailID) {
        orderDetailsList.removeIf(od -> od.getOrderDetailID() == orderDetailID);
    }

    @Override
    public List<OrderDetails> getAllOrderDetails() {
        return orderDetailsList;
    }

    @Override
    public OrderDetails getOrderDetailById(int orderDetailID) {
        for (OrderDetails od : orderDetailsList) {
            if (od.getOrderDetailID() == orderDetailID) {
                return od;
            }
        }
        return null;
    }
}
