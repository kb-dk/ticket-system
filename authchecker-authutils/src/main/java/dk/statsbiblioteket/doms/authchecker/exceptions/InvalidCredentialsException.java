package dk.statsbiblioteket.doms.authchecker.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 11:18:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidCredentialsException extends BackendException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
