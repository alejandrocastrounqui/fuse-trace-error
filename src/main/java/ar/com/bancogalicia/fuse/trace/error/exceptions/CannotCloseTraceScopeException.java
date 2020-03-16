package ar.com.bancogalicia.fuse.trace.error.exceptions;

public class CannotCloseTraceScopeException extends RuntimeException {

	private static String defaultMessage = "It is not possible to clean tracing scope";

	private static final long serialVersionUID = 1L;

	public CannotCloseTraceScopeException() {
		super(defaultMessage);
	}

	public CannotCloseTraceScopeException(Throwable cause) {
		super(defaultMessage, cause);
	}

}
