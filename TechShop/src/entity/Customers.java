package entity;

import exception.InvalidDataException;

public class Customers {
    private int CustomerID;
    private String FirstName;
    private String LastName;
    private String Email;
    private String Phone;
    private String Address;

    public Customers(int customerID, String firstName, String lastName, String email, String phone, String address) {
        setCustomerID(customerID);
        setFirstName(firstName);
        setLastName(lastName);
        try {
           setEmail(email);
           setPhone(phone);
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }

        setAddress(address);
    }

    public Customers() {
		// TODO Auto-generated constructor stub
	}

	public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        if (customerID > 0) this.CustomerID = customerID;
        else throw new IllegalArgumentException("Customer ID must be positive.");
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) this.FirstName = firstName;
        else throw new IllegalArgumentException("First name cannot be null or empty.");
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) this.LastName = lastName;
        else throw new IllegalArgumentException("Last name cannot be null or empty.");
    }

    public String getEmail() {
        return Email;
    }
 
     public  void setEmail(String email) throws InvalidDataException {
     if (email != null && email.contains("@")) {
         this.Email = email;
     } else {
         throw new InvalidDataException("Invalid email address. Please enter a valid email.");
     }
     }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) throws InvalidDataException {
        if (phone != null && phone.length() >= 10) {
            this.Phone = phone;
        } else {
            throw new InvalidDataException("Phone number must be at least 10 digits.");
        }
    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        if (address != null && !address.trim().isEmpty()) this.Address = address;
        else throw new IllegalArgumentException("Address cannot be null or empty.");
    }

    public void GetCustomerDetails() {
        System.out.println("Customer ID : " + CustomerID);
        System.out.println("Name        : " + FirstName + " " + LastName);
        System.out.println("Email       : " + Email);
        System.out.println("Phone       : " + Phone);
        System.out.println("Address     : " + Address);
    }

    public void UpdateCustomerInfo(String email, String phone, String address) {
    	try {
    	    setEmail(email);
    	    setPhone(phone);
    	} catch (InvalidDataException e) {
    	    System.out.println("Error: " + e.getMessage());
    	}

        setAddress(address);
    }

    public int CalculateTotalOrders() {
        return 0; 
    }

	
   
}
