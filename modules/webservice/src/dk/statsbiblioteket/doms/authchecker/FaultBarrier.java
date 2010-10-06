package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.exceptions.*;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 10:42:08 AM
 * To change this template use File | Settings | File Templates.
 */
@Provider
public class FaultBarrier implements ExceptionMapper<BackendException>{

    public Response toResponse(BackendException exception) {
        if (exception instanceof InvalidCredentialsException){
            return Response.status(ClientResponse.Status.FORBIDDEN).entity(exception.getMessage()+": Wrong credentials supplied, failure").build();
        } else if (exception instanceof URLNotFoundException || exception instanceof ResourceNotFoundException){
            return Response.status(ClientResponse.Status.NOT_FOUND).entity(exception.getMessage() + ": Resource not found, failure").build();
        } else if (exception instanceof FedoraException){
            return Response.serverError().entity(exception.getMessage()+": Fedora error, failure").build();
        } else if (exception instanceof UserNotFoundException){
            return Response.status(ClientResponse.Status.FORBIDDEN).entity(exception.getMessage()+": User not found, failure").build();
        }
        return Response.serverError().entity(exception.getMessage()+
                                             ": Generic failure, see server logs.").build();
    }

}
