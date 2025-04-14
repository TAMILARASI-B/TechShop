package exception;

public class IncompleteOrderDetailException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public IncompleteOrderDetailException(String message) {
        super(message);
    }
}