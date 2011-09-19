package dk.statsbiblioteket.doms.authchecker.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 11:36:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraException extends BackendException {
    public FedoraException(String message) {
        super(message);
    }

    public FedoraException(String message, Throwable cause) {
        super(message, cause);
    }
}
