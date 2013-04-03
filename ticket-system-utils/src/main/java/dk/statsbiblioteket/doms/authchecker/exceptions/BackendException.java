package dk.statsbiblioteket.doms.authchecker.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 10:43:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class BackendException extends Exception{

    public BackendException(String message) {
        super(message);
    }

    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
