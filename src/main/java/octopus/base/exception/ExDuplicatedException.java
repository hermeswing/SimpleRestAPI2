package octopus.base.exception;

public class ExDuplicatedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExDuplicatedException( String msg, Throwable t) {
		super(msg, t);
	}

	public ExDuplicatedException( String msg) {
		super(msg);
	}

	public ExDuplicatedException() {
		super();
	}
}