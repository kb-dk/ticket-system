package dk.statsbiblioteket.medieplatform.ticketsystem;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 8, 2010
 * Time: 4:48:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketNotFoundException extends BackendException {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
