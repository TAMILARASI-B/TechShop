package dao;

import java.io.IOException;
import java.util.List;

import entity.Products;
import exception.DuplicateEntryException;

public interface ProductDAO {
    void addProduct(Products product) throws DuplicateEntryException, IOException;
    void updateProduct(Products product) throws IOException;
    void removeProduct(int productID);
    void markProductAsOrdered(int productID);
    void listAllProducts();
	List<Products> getAllProducts();
	List<Products> searchProductsByName(String name);
	List<Products> searchProductsByPriceRange(double minPrice, double maxPrice);
	Products getProductById(int productID) throws IOException;
}