package dk.statsbiblioteket.doms.authchecker.exceptions;



/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 12:01:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceNotFoundException extends BackendException{
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

