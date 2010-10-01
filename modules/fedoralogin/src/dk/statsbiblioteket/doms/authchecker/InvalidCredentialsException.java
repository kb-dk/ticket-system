package dk.statsbiblioteket.doms.authchecker;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 11:18:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
