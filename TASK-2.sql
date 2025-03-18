-- 1. Write an SQL query to retrieve the names and emails of all customers.

SELECT FirstName,LastName,Email FROM Customers;
SELECT CONCAT(FirstName, ' ', LastName) AS CustomerName, Email FROM Customers;

/* 2. Write an SQL query to list all orders with their order dates and corresponding customer
names.*/

SELECT Orders.OrderID, Orders.OrderDate, CONCAT(Customers.FirstName, ' ', Customers.LastName) AS CustomerName FROM Orders
JOIN Customers ON Orders.CustomerID = Customers.CustomerID;

/* 3. Write an SQL query to insert a new customer record into the "Customers" table. Include
customer information such as name, email, and address.*/

INSERT INTO Customers (CustomerID, FirstName, LastName, Email, Phone, Address) 
VALUES (113, 'Rahul', 'Sharma', 'rahul.sharma@gmail.com', '9876543211', '5/10 XYZ Colony, Bangalore, 560001');

/* 4. Write an SQL query to update the prices of all electronic gadgets in the "Products" table by
increasing them by 10%.*/

SET SQL_SAFE_UPDATES=0;
UPDATE Products
SET Price = Price * 1.10;

/*5. Write an SQL query to delete a specific order and its associated order details from the
"Orders" and "OrderDetails" tables. Allow users to input the order ID as a parameter.*/

DELETE FROM OrderDetails WHERE OrderID = 108;
DELETE FROM Orders WHERE OrderID = 108;

/*6. Write an SQL query to insert a new order into the "Orders" table. Include the customer ID,
order date, and any other necessary information.*/

INSERT INTO Orders (OrderID, CustomerID, OrderDate, TotalAmount)  
VALUES (13, 105, '2025-03-18', 7500);

/*7. Write an SQL query to update the contact information (e.g., email and address) of a specific
customer in the "Customers" table. Allow users to input the customer ID and new contact
information.*/

UPDATE Customers
SET Email = 'banu01@gmail.com', 
    Address = '12/23 HJO Colony,Trichy,621014'
WHERE CustomerID= 101;

/*8. Write an SQL query to recalculate and update the total cost of each order in the "Orders"
table based on the prices and quantities in the "OrderDetails" table.*/

SET SQL_SAFE_UPDATES=0;
UPDATE Orders
SET TotalAmount = (
    SELECT SUM(od.Quantity * p.Price)
    FROM OrderDetails od
    JOIN Products p ON od.ProductID = p.ProductID
    WHERE od.OrderID = Orders.OrderID
);

/*9. Write an SQL query to delete all orders and their associated order details for a specific
customer from the "Orders" and "OrderDetails" tables. Allow users to input the customer ID
as a parameter.*/

DELETE FROM OrderDetails 
WHERE OrderID IN (SELECT OrderID FROM Orders WHERE CustomerID = 104);
DELETE FROM Orders 
WHERE CustomerID = 104;

/*10. Write an SQL query to insert a new electronic gadget product into the "Products" table,
including product name, category, price, and any other relevant details.*/

INSERT INTO Products (ProductID, ProductName, Description, Price)
VALUES (13, 'Wireless Earbuds', 'Bluetooth 5.0, Noise-Canceling, 24h Battery', 6000);

/*11. Write an SQL query to update the status of a specific order in the "Orders" table (e.g., from
"Pending" to "Shipped"). Allow users to input the order ID and the new status.*/

ALTER TABLE Orders ADD COLUMN Status VARCHAR(20) DEFAULT 'Pending';
UPDATE Orders
SET Status = 'Shipped'
WHERE OrderID = 8;

/*12. Write an SQL query to calculate and update the number of orders placed by each customer
in the "Customers" table based on the data in the "Orders" table.*/

ALTER TABLE Customers ADD COLUMN TotalOrders INT DEFAULT 0;
UPDATE Customers
SET TotalOrders = (
    SELECT COUNT(*)
    FROM Orders
    WHERE Orders.CustomerID = Customers.CustomerID
);


