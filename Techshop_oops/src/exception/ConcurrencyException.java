package exception;

public class ConcurrencyException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public ConcurrencyException(String message) {
        super(message);
    }
}