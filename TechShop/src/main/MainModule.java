package main;
import entity.*;
import util.*;
import java.util.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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

                ProductDAO productDAO2 = new ProductDAOImpl();  
                try {
                    productDAO2.removeProduct(deleteProductID);  
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

    
        Customers customer = new Customers(customerID, null, null, null, null, null);

        System.out.print("Enter number of products in the order: ");
        int count = scanner.nextInt();
        List<OrderDetails> orderDetailsList = new ArrayList<>();

        ProductDAO productDAO = new ProductDAOImpl();

        for (int i = 0; i < count; i++) {
            System.out.print("Enter Product ID: ");
            int productID = scanner.nextInt();

        
            Products product = productDAO.getProductById(productID);

            if (product != null) {
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();

             
                double price = product.getPrice() * quantity;

                
                orderDetailsList.add(new OrderDetails(0, product, quantity, price));
            } else {
                System.out.println("Product not found.");
            }
        }


        Orders order = new Orders(customer, LocalDate.now(), count, orderDetailsList);


        OrderDAO dao = new OrderDAOImpl();
        dao.placeOrder(order, orderDetailsList);
    }

    private static void handleTrackOrderStatus(Scanner scanner) throws IOException, SQLException {
        System.out.println("\n--- Track Order Status ---");
        System.out.println("Enter Order ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();  

        OrderDAO orderDAO = new OrderDAOImpl();  
        Orders order = orderDAO.getOrderById(orderID);

       
            System.out.println("Order Status: " + order.getStatus());
        
    }


    private static void handleInventoryManagement(Scanner scanner) throws IOException {
        System.out.println("\n--- Inventory Management ---");
        System.out.println("Enter Inventory ID: ");
        int inventoryID = scanner.nextInt();
        
        System.out.println("Enter Product ID: ");
        int productID = scanner.nextInt();   
        System.out.println("Enter Quantity to Add: ");
        int quantity = scanner.nextInt();

        InventoryDAO inventoryDAO = new InventoryDAOImpl();
        inventoryDAO.addProductToInventory(inventoryID, productID, quantity);
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

        System.out.println("Enter Order ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();

        OrderDAO orderDAO = new OrderDAOImpl();
        Orders order = orderDAO.getOrderById(orderID);

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        double totalAmount = order.getTotalAmount(); // ✅ fetched from DB
        System.out.println("Total amount to be paid: $" + totalAmount);

        System.out.print("Enter payment amount: ");
        double paidAmount = scanner.nextDouble();
        scanner.nextLine();

        if (paidAmount >= totalAmount) {
            System.out.println("Payment successful. Change: $" + (paidAmount - totalAmount));
            order.setStatus("Paid");
            orderDAO.updateOrderStatus(orderID, "Paid");
        } else {
            System.out.println("Payment failed. Insufficient amount.");
        }
    }

    private static void handleProductSearchAndRecommendations(Scanner scanner) {
        System.out.println("\n--- Product Search and Recommendations ---");
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        ProductDAO productDAO = new ProductDAOImpl();

        List<Products> searchResults = productDAO.searchProductsByName(keyword);

        if (searchResults.isEmpty()) {
            System.out.println("No products found with keyword: " + keyword);
        } else {
            System.out.println("\nSearch Results:");
            for (Products p : searchResults) {
                p.getProductDetails();
                System.out.println("-------------------------");
            }

            double basePrice = searchResults.get(0).getPrice();
            double minPrice = basePrice - 50;
            double maxPrice = basePrice + 50;

            System.out.println("\nRecommended Products (Price Range: " + minPrice + " to " + maxPrice + "):");
            List<Products> recommended = productDAO.searchProductsByPriceRange(minPrice, maxPrice);

            for (Products p : recommended) {
                if (!searchResults.contains(p)) { 
                    p.getProductDetails();
                    System.out.println("-------------------------");
                }
            }
        }
    }

}
