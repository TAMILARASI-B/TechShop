package main;
import entity.*;
import java.time.format.DateTimeFormatter;
import util.*;
import java.util.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

import exception.*;
import dao.*;
public class MainModule {

    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- TechShop System ---");
            System.out.println("1. Customer Registration");
            System.out.println("2. Product Catalog Management");
            System.out.println("3. Place Order");
            System.out.println("4. Track Order Status");
            System.out.println("5. Inventory Management");
            System.out.println("6. Sales Reporting");
            System.out.println("7. Update Customer Account");
            System.out.println("8. Payment Processing");
            System.out.println("9. Product Search and Recommendations");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  

            switch (choice) {
                case 1:
                    
                    handleCustomerRegistration(scanner);
                    break;

                case 2:
                    
                    handleProductCatalogManagement(scanner);
                    break;

                case 3:
                    
                    handlePlaceOrder(scanner);
                    break;

                case 4:

                    handleTrackOrderStatus(scanner);
                    break;

                case 5:
                   
                    handleInventoryManagement(scanner);
                    break;

                case 6:
                   
                    handleSalesReporting(scanner);
                    break;

                case 7:
                    
				try {
					handleUpdateCustomerAccount(scanner);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    break;

                case 8:
                    
                    handlePaymentProcessing(scanner);
                    break;

                case 9:
                    
                    handleProductSearchAndRecommendations(scanner);
                    break;

                case 0:
                    System.out.println("Exiting TechShop System.");
                    return;  

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleCustomerRegistration(Scanner scanner) {
        System.out.println("\n--- Customer Registration ---");
        System.out.println("Enter CustomerId: ");
        int CustomerID = scanner.nextInt();
        scanner.nextLine(); 
        System.out.println("Enter FirstName: ");
        String FirstName = scanner.nextLine();
        System.out.println("Enter LastName: ");
        String LastName = scanner.nextLine();
        System.out.println("Enter Email: ");
        String Email= scanner.nextLine();
       
        System.out.println("Enter Phone: ");
        String Phone = scanner.nextLine();
        System.out.println("Enter Address: ");
        String Address= scanner.nextLine();

        Customers newCustomer = new Customers(CustomerID, FirstName,LastName, Email, Phone, Address);
        CustomerDAO customerDAO = new CustomerDAOImpl();

        try {
            customerDAO.registerCustomer(newCustomer); 
        } catch (DuplicateEntryException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

   
    private static void handleProductCatalogManagement(Scanner scanner) throws IOException {
        System.out.println("\n--- Product Catalog Management ---");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  

        switch (choice) {
            case 1:
            	System.out.println("Enter Product	ID: ");
                int productID = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter Product Name: ");
                String productName = scanner.nextLine();
                System.out.println("Enter Description: ");
                String description = scanner.nextLine();
                System.out.println("Enter Price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();  
                Products newProduct = new Products(productID, productName, description, price);
                ProductDAO productDAO = new ProductDAOImpl();

                try {
                    productDAO.addProduct(newProduct);  
                } catch (DuplicateEntryException | IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;

            case 2:
                System.out.println("Enter Product ID to Update: ");
                int productID1 = scanner.nextInt();
                scanner.nextLine();  
                System.out.println("Enter Updated Product Name: ");
                String updatedName = scanner.nextLine();
                System.out.println("Enter Updated Price: ");
                double updatedPrice = scanner.nextDouble();
                scanner.nextLine(); 
                System.out.println("Enter Updated Description: ");
                String updatedDescription = scanner.nextLine();
              
                Products updatedProduct = new Products(productID1, updatedName, updatedDescription, updatedPrice);
                ProductDAO productDAO1 = new ProductDAOImpl();

                try {
                    productDAO1.updateProduct(updatedProduct);  
                } catch (DuplicateEntryException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;

            case 3:
                System.out.println("Enter Product ID to Delete: ");
                int deleteProductID = scanner.nextInt();
                scanner.nextLine();  
                System.out.println("Enter quantity to Delete: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();  

                ProductDAO productDAO2 = new ProductDAOImpl();  
                try {
                    productDAO2.removeProduct(deleteProductID,quantity);  
                } catch (ProductNotFoundException | ProductInUseException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;


            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    private static void handlePlaceOrder(Scanner scanner) throws SQLException, IOException {
        System.out.println("--- Place Order ---");
        System.out.print("Enter Customer ID: ");
        int customerID = scanner.nextInt();

        CustomerDAO customerDAO = new CustomerDAOImpl(); 
        Customers customer = customerDAO.getCustomerById(customerID);

        if (customer == null) {
            System.out.println("❌ Customer not found. Please check the Customer ID.");
            return;
        }

        System.out.print("Enter number of products in the order: ");
        int count = scanner.nextInt();
        List<OrderDetails> orderDetailsList = new ArrayList<>();

        ProductDAO productDAO = new ProductDAOImpl();
        InventoryDAO inventoryDAO = new InventoryDAOImpl(); 

        double totalAmount = 0.0; 

        for (int i = 0; i < count; i++) {
            System.out.print("Enter Product ID: ");
            int productID = scanner.nextInt();

            Products product = productDAO.getProductById(productID);

            if (product != null) {
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();

                int availableStock = inventoryDAO.getProductStock(productID);

                if (quantity > availableStock) {
                    System.out.println("Only " + availableStock + " units available in stock.");
                    System.out.print("Do you want to place order for available quantity? (yes/no): ");
                    String choice = scanner.next();

                    if (choice.equalsIgnoreCase("yes")) {
                        quantity = availableStock;
                    } else {
                        System.out.println("Skipping product: " + product.getProductName());
                        continue;  
                    }
                }

                double price = product.getPrice() * quantity; 
                totalAmount += price;  

                orderDetailsList.add(new OrderDetails(0, product, quantity, price));
            } else {
                System.out.println("Product not found.");
            }
        }
        if (orderDetailsList.isEmpty()) {
            System.out.println("❌ No products selected. Order was not placed.");
            return;
        }

        Orders order = new Orders(customer, LocalDateTime.now(), totalAmount, "Processing"); 
        order.setOrderDetails(orderDetailsList);

        OrderDAO dao = new OrderDAOImpl();
        dao.placeOrder(order, orderDetailsList); 
    }
    private static void handleTrackOrderStatus(Scanner scanner) throws IOException, SQLException {
        System.out.println("\n--- Track Order Status ---");
        System.out.print("Enter Order ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();

        OrderDAO orderDAO = new OrderDAOImpl();  
        Orders order = orderDAO.getOrderById(orderID);

        if (order != null) {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = order.getOrderDate().format(formatter);
            System.out.println("\n📦 Order Details:");
            System.out.println("Order ID     : " + order.getOrderID());
            System.out.println("Order Date   : " + formattedDate);
            System.out.println("Total Amount : $" + order.getTotalAmount());
            System.out.println("Status       : " + order.getStatus());
        } else {
            System.out.println("❌ Order not found for ID: " + orderID);
        }
    }

    private static void handleInventoryManagement(Scanner scanner) throws IOException, SQLException {
        InventoryDAO inventoryDAO = new InventoryDAOImpl();
        while (true) {
            System.out.println("\n--- 🗂 Inventory Management ---");
            System.out.println("1. Add Product to Inventory");
            System.out.println("2. Remove Product from Inventory");
            System.out.println("3. View All Products in Inventory");
            System.out.println("4. Go Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1: 
                    System.out.print("Enter Inventory ID: ");
                    int addInventoryID = scanner.nextInt();
                    System.out.print("Enter Product ID: ");
                    int addProductID = scanner.nextInt();
                    System.out.print("Enter Quantity to Add: ");
                    int addQuantity = scanner.nextInt();

                    if (addQuantity <= 0) {
                        System.out.println("❌ Quantity must be greater than 0.");
                        break;
                    }

                    if (inventoryDAO.addProductToInventory(addInventoryID, addProductID, addQuantity)) {
                        System.out.println("✅ Product added to inventory.");
                    } else {
                        System.out.println("❌ Failed to add product. Check IDs.");
                    }
                    break;

                case 2: 
                    System.out.print("Enter Inventory ID: ");
                    int removeInventoryID = scanner.nextInt();
                    System.out.print("Enter Product ID: ");
                    int removeProductID = scanner.nextInt();
                    System.out.print("Enter Quantity to Remove: ");
                    int removeQuantity = scanner.nextInt();

                    if (removeQuantity <= 0) {
                        System.out.println("❌ Quantity must be greater than 0.");
                        break;
                    }

                    if (inventoryDAO.removeProductFromInventory(removeInventoryID, removeProductID, removeQuantity)) {
                        System.out.println("✅ Product removed from inventory.");
                    } else {
                        System.out.println("❌ Failed to remove product. Not enough quantity or invalid ID.");
                    }
                    break;

                case 3:
                    System.out.println("\n📦 All Products in Inventory:");
                    List<Inventory> allInventory = inventoryDAO.getInventoryList();
                    for (Inventory inv : allInventory) {
                      
                        Products prod = inv.getProduct(); 
                        System.out.println("Product ID: " + prod.getProductID() +
                                " | Name: " + prod.getProductName() +
                                " | Quantity: " + inv.getQuantityInStock());
                    }
                    break;

                case 4: 
                    return;

                default:
                    System.out.println("❌ Invalid choice. Please select a valid option.");
            }
        }
    }


    private static void handleSalesReporting(Scanner scanner) throws IOException {
        System.out.println("\n--- Sales Reporting ---");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
        	conn = DBConnUtil.getDbConnection();

            String sql = "SELECT COUNT(*) AS totalOrders, SUM(TotalAmount) AS totalSales FROM Orders";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int totalOrders = rs.getInt("totalOrders");
                double totalSales = rs.getDouble("totalSales");

                System.out.println("Total Orders   : " + totalOrders);
                System.out.println("Total Sales    : $" + totalSales);
            } else {
                System.out.println("No orders found.");
            }

        } catch (SQLException e) {
            System.out.println("Error generating sales report: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }


    private static void handleUpdateCustomerAccount(Scanner scanner) throws Exception {
        System.out.println("\n--- Update Customer Account ---");
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();

        CustomerDAO customerDAO = new CustomerDAOImpl();
        Customers existingCustomer = customerDAO.getCustomerById(customerId);

        if (existingCustomer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter New Email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter New Phone: ");
        String newPhone = scanner.nextLine();

       
        existingCustomer.setEmail(newEmail);
        existingCustomer.setPhone(newPhone);

        customerDAO.updateCustomerAccount(existingCustomer);
 
    }


    private static void handlePaymentProcessing(Scanner scanner) throws IOException, SQLException {
        System.out.println("\n--- Payment Processing ---");
        System.out.print("Enter Order ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();  
        
        OrderDAO orderDAO = new OrderDAOImpl();
        Orders order = orderDAO.getOrderById(orderID);
        
        if (order == null) {
            System.out.println("❌ Order not found with ID: " + orderID);
            return; 
        }

        System.out.println("Total amount to be paid: $" + order.getTotalAmount());

        System.out.print("Enter payment amount: ");
        double paymentAmount = scanner.nextDouble();
        
        if (paymentAmount < order.getTotalAmount()) {
            System.out.println("❌ Insufficient payment. Please pay the full amount.");
            return;
        }
        
        double change = paymentAmount - order.getTotalAmount();
        System.out.println("Payment successful. Change: $" + change);
     
        try {
            order.setStatus("Paid");
            orderDAO.updateOrderStatus(order);
            System.out.println("✅ Order status updated to 'Paid'.");
        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }


    private static void handleProductSearchAndRecommendations(Scanner scanner) throws IOException {
        System.out.println("\n--- Product Search and Recommendations ---");
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();
        ProductDAO productDAO = new ProductDAOImpl();
        List<Products> matchingProducts = productDAO.searchProductsByKeyword(keyword);

        if (matchingProducts.isEmpty()) {
            System.out.println("No products found with keyword: " + keyword);
        } else {
            System.out.println("\n🔍 Search Results:");
            for (Products product : matchingProducts) {
                System.out.println("Product ID: " + product.getProductID() +
                                   " | Name: " + product.getProductName() +
                                   " | Price: $" + product.getPrice());
            }
        }

}
}