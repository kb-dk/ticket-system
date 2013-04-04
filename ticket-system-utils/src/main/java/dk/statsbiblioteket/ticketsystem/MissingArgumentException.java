package dk.statsbiblioteket.ticketsystem;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/28/11
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MissingArgumentException extends BackendException{
    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
