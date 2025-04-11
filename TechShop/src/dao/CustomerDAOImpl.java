package dao;

import entity.Customers;
import exception.DuplicateEntryException;
import util.DBConnUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public void registerCustomer(Customers customer) throws DuplicateEntryException, IOException {
        if (isEmailExists(customer.getEmail())) {
            throw new DuplicateEntryException("Email address already registered.");
        }

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            
        	connection = DBConnUtil.getDbConnection();

            String insertQuery = "INSERT INTO Customers (CustomerID, FirstName, LastName, Email, Phone, Address) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, customer.getCustomerID());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getAddress());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer registered successfully.");
            } else {
                System.out.println("Customer registration failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error while registering customer: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error while closing resources: " + e.getMessage());
            }
        }
    }

   
    @Override
    public boolean isEmailExists(String email) {
       
        return false;  
    }

    @Override
    public Customers getCustomerById(int customerID) throws IOException {
        Customers customer = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String query = "SELECT * FROM Customers WHERE CustomerID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, customerID);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String address=rs.getString("Address");

                customer = new Customers(customerID, firstName, lastName, email, phone,address);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching customer: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return customer;
    }

    @Override
    public void updateCustomerAccount(Customers customer) throws IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getDbConnection();
            String query = "UPDATE Customers SET Email = ?, Phone = ? WHERE CustomerID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPhone());
            stmt.setInt(3, customer.getCustomerID());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Customer account updated successfully.");
            } else {
                System.out.println("No customer found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }



    
}
