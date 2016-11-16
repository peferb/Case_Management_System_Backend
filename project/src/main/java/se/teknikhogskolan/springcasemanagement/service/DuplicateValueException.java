package se.teknikhogskolan.springcasemanagement.service;

public final class DuplicateValueException extends ServiceException {

    private static final long serialVersionUID = 3829172638473629182L;

    public DuplicateValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateValueException(String message) {
        super(message);
    }

    public DuplicateValueException() {
    }
}
