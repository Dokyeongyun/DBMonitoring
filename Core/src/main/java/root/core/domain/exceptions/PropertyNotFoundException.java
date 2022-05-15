package root.core.domain.exceptions;

public class PropertyNotFoundException extends Exception {
	private static final long serialVersionUID = 196259383529491976L;
	
	public PropertyNotFoundException(String message) {
		super(message);
	}
}
