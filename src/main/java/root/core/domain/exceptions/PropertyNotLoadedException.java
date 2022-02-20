package root.core.domain.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PropertyNotLoadedException extends Exception {

	private static final long serialVersionUID = -872053637537601527L;

	public PropertyNotLoadedException(String propName) {
		super(propName);
	}
}