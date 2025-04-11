package dao;

import entity.Inventory;

import java.io.IOException;
import java.util.List;

public interface InventoryDAO {
    void addInventory(Inventory inventory);
    void removeFromInventory(int inventoryID, int quantity);
    void addToInventory(int inventoryID, int quantity);
    void updateStock(int inventoryID, int newQuantity);
    void listLowStockProducts(int threshold);
    void listOutOfStockProducts();
    void listAllProducts();
    List<Inventory> getInventoryList();
    static boolean isProductAvailable(int productID, int requiredQuantity) {
		// TODO Auto-generated method stub
		return false;
	}
    void addProductToInventory(int inventoryID,int productID, int quantity) throws IOException;
	
}

