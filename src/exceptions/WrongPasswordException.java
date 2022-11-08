package exceptions;

public class WrongPasswordException extends BaseException {

    public WrongPasswordException(String message) {
        super(message);
    }
}
