package dk.statsbiblioteket.medieplatform.ticketsystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This Fault barrier handles the conversion of BackendExceptions into http responses.
 */
@Provider
public class FaultBarrier implements ExceptionMapper<BackendException>{

    private Log log = LogFactory.getLog(FaultBarrier.class);

    public Response toResponse(BackendException exception) {
        if (exception instanceof TicketNotFoundException){
            return Response.status(Response.Status.GONE).entity(exception.getMessage()+": The ticket could not be found").build();
        } else if (exception instanceof MissingArgumentException){
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()+": Missing argument").build();
        }
        log.warn("Caught unknown exception, review how this got here",exception);
        return Response.serverError().entity(exception.getMessage()+
                ": Generic failure, see server logs.").build();
    }

}
