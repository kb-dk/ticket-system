package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.exceptions.*;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;

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
        //TODO
        if (exception instanceof InvalidCredentialsException){

        } else if (exception instanceof URLNotFoundException || exception instanceof ResourceNotFoundException){

        } else if (exception instanceof FedoraException){

        } else if (exception instanceof UserNotFoundException){
            
        }
        return Response.serverError().entity(exception.getMessage()+
                                             ": Generic failure, see server logs.").build();
    }

}
