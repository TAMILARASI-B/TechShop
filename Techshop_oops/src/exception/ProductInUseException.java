
package exception;

public class ProductInUseException extends RuntimeException {
    	private static final long serialVersionUID = 1L;

	public ProductInUseException(String message) {
        super(message);
    }
}