package dao;

import entity.Inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface InventoryDAO {
    void addInventory(Inventory inventory);
    void removeFromInventory(int inventoryID, int quantity) throws IOException;
    void addToInventory(int inventoryID, int quantity) throws IOException;
    void updateStock(int inventoryID, int newQuantity) throws IOException;
    void listLowStockProducts(int threshold);
    void listOutOfStockProducts();
    void listAllProducts();
    List<Inventory> getInventoryList() throws SQLException, IOException;
    static boolean isProductAvailable(int productID, int requiredQuantity) {
		// TODO Auto-generated method stub
		return false;
	}
    boolean addProductToInventory(int inventoryID,int productID, int quantity) throws IOException;
    int getProductStock(int productID) throws SQLException, IOException;
    boolean removeProductFromInventory(int inventoryID, int productID, int quantity) throws IOException;
    Inventory findInventoryById(int inventoryID) throws IOException;

    
}
