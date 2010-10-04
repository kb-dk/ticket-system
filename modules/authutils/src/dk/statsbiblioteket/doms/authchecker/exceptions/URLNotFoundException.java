package dk.statsbiblioteket.doms.authchecker.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 11:35:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLNotFoundException extends BackendException {
    public URLNotFoundException(String message) {
        super(message);
    }

    public URLNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
