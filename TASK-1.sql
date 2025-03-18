CREATE DATABASE TechShop;
USE TechShop;
CREATE TABLE Customers(CustomerID INT PRIMARY KEY,FirstName VARCHAR(50),
LastName VARCHAR(50),Email VARCHAR(50),Phone VARCHAR(15),Address VARCHAR(50));
CREATE TABLE Products(ProductID INT PRIMARY KEY,ProductName VARCHAR(50),Description VARCHAR(255),Price INT);
CREATE TABLE Orders(OrderID INT PRIMARY KEY,CustomerID INT,FOREIGN KEY(CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
OrderDate DATE,TotalAmount DECIMAL(10,2));
CREATE TABLE OrderDetails(OrderDetailID INT PRIMARY KEY,OrderID INT,FOREIGN KEY(OrderID) REFERENCES Orders(OrderID),ProductID INT,
FOREIGN KEY(ProductID) REFERENCES Products(ProductID),Quantity INT);
CREATE TABLE Inventory(InventoryID INT PRIMARY KEY,ProductID INT,FOREIGN KEY(ProductID) REFERENCES Products(ProductID),
QuantityInStock INT,LastStockUpdate DATE);
INSERT INTO Customers(CustomerID,FirstName,LastName,Email,Phone,Address) VALUES
(101,'Aarya','Khedekar','aarya01@gmail.com','9123456780','1/2 NGO Colony,Madurai,636122'),
(102,'Aayush','Tripathi','aayush02@gmail.com','9876543210','2/1 KPR Colony,Tiruppur,638111'),
(103,'Ajay','Kumar','ajay03@gmail.com','9786534210','10/23 FGO Colony,Karur,639111'),
(104,'Asha','Janet','asha04@gmail.com','9976524312','12/4 JNM Colony,Chennai,600090'),
(105,'Kamali','Kanmani','kamali05@gmail.com','9768541320','1/4 BGP Colony,Erode,638401'),
(106,'Mohammed','Faizulla','mohammed05@gmail.com','9812365470','12/34 GHM Colony,Tiruppur,638111'),
(107,'Sharmiladevi','Saravanan','sharmila06@gmail.com','9768352410','8/167 KHM Colony,Coimbatore,641652'),
(108,'Vibhavari','Chourasia','vibhavari08@gmail.com','9987665510','23/34 CPK Colony,Dindigal,624308'),
(109,'Rituja','Yadav','rituja09@gmail.com','9712376450','1/4 BHK Colony,Trichy,621014'),
(110,'Leena','Sri','leena10@gmail.com','9817263540','1/2 FVH Colony,Villupuram,605758'),
(111,'Sri','Subiksha','srisha11@gmail.com','9807615248','2/6 BNP Colony,Salem,636122'),
(112,'Monisha','Muthu','monisha12@gmail.com','9917823046','1/30 SNJ Colony,Erode,638401');
INSERT INTO Products (ProductID, ProductName, Description, Price) VALUES
(1, 'Laptop', '14-inch, 8GB RAM', 50000),
(2, 'Smartphone', '6.5-inch, 128GB', 20000),
(3, 'Headphones', 'Wireless, Noise-Canceling', 3000),
(4, 'Keyboard', 'Mechanical, RGB', 2500),
(5, 'Mouse', 'Wireless, Optical', 1200),
(6, 'Smartwatch', 'Fitness Tracker, AMOLED', 8000),
(7, 'Tablet', '10-inch, 64GB', 18000),
(8, 'Monitor', '24-inch, 144Hz', 15000),
(9, 'Printer', 'Laser, Duplex', 12000),
(10, 'External HDD', '1TB, USB 3.0', 4500),
(11, 'Speakers', 'Bluetooth, 10W', 3500),
(12, 'Webcam', '1080p, Autofocus', 2800);
INSERT INTO Orders (OrderID, CustomerID, OrderDate, TotalAmount) VALUES
(1,101, '2024-03-01', 50000),
(2,102, '2024-03-02', 20000),
(3,103, '2024-03-03', 3000),
(4,104, '2024-03-04', 2500),
(5,105, '2024-03-05', 1200),
(6,106, '2024-03-06', 8000),
(7,107, '2024-03-07', 18000),
(8,108, '2024-03-08', 15000),
(9,109, '2024-03-09', 12000),
(10,110, '2024-03-10', 4500),
(11,111, '2024-03-11', 3500),
(12,112, '2024-03-12', 2800);
INSERT INTO OrderDetails (OrderDetailID, OrderID, ProductID, Quantity) VALUES
(1, 1, 1, 1),   
(2, 2, 2, 2),   
(3, 3, 3, 1),   
(4, 4, 4, 1),   
(5, 5, 5, 2),   
(6, 6, 6, 1),  
(7, 7, 7, 1),   
(8, 8, 8, 1),  
(9, 9, 9, 1),   
(10, 10, 10, 1), 
(11, 11, 11, 2), 
(12, 12, 12, 1);
INSERT INTO Inventory (InventoryID, ProductID, QuantityInStock, LastStockUpdate) VALUES
(1, 1, 10, '2024-03-01'), 
(2, 2, 15, '2024-03-02'),  
(3, 3, 25, '2024-03-03'),
(4, 4, 20, '2024-03-04'), 
(5, 5, 30, '2024-03-05'), 
(6, 6, 12, '2024-03-06'),
(7, 7, 18, '2024-03-07'), 
(8, 8, 10, '2024-03-08'), 
(9, 9, 8, '2024-03-09'), 
(10, 10, 14, '2024-03-10'),
(11, 11, 22, '2024-03-11'),
(12, 12, 16, '2024-03-12');