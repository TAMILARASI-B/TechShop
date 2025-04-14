package dao;

import java.io.IOException;

import entity.Customers;
import exception.*;

public interface CustomerDAO {
    
    void registerCustomer(Customers customer) throws DuplicateEntryException, IOException;
    boolean isEmailExists(String email);
	Customers getCustomerById(int customerId) throws IOException;
	void updateCustomerAccount(Customers customer) throws IOException;
}