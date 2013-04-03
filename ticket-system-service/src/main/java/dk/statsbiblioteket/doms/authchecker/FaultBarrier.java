package dk.statsbiblioteket.doms.authchecker;

import com.sun.jersey.api.client.ClientResponse;
import dk.statsbiblioteket.doms.authchecker.exceptions.*;
import dk.statsbiblioteket.doms.authchecker.exceptions.TicketNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 10:42:08 AM
 * To change this template use File | Settings | File Templates.
 */
@Provider
public class FaultBarrier implements ExceptionMapper<BackendException>{

    private Log log = LogFactory.getLog(FaultBarrier.class);

    public Response toResponse(BackendException exception) {
        if (exception instanceof TicketNotFoundException){
            return Response.status(ClientResponse.Status.GONE).entity(exception.getMessage()+": The ticket could not be found").build();
        } else if (exception instanceof MissingArgumentException){
            return Response.status(ClientResponse.Status.BAD_REQUEST).entity(exception.getMessage()+": Missing argument").build();
        }
        log.warn("Caught unknown exception, review how this got here",exception);
        return Response.serverError().entity(exception.getMessage()+
                ": Generic failure, see server logs.").build();
    }

}
