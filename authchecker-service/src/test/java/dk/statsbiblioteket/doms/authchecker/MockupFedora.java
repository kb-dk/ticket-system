package dk.statsbiblioteket.doms.authchecker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Mockup rest webservice that will respond to fedora datastream requests.
 * Given any datastream request, will result in an empty http response, with response code based on the given pid:
 * PID demo:1 - HTTP status code OK
 * PID demo:2 - HTTP status code NOT_FOUND
 * PID demo:3 - HTTP status code UNAUTHORIZED
 * PID demo:4 - HTTP status code INTERNAL_SERVER_ERROR
 * Any other  - HTTP status code INTERNAL_SERVER_ERROR
 */
@Path("/objects")
public class MockupFedora {
    /**
     * Mockup of Fedora get datastream method.
     * Respond to all requests with status codes:
     * <ul>
     * <li>PID demo:1 - HTTP status code OK</li>
     * <li>PID demo:2 - HTTP status code NOT_FOUND</li>
     * <li>PID demo:3 - HTTP status code UNAUTHORIZED</li>
     * <li>PID demo:4 - HTTP status code INTERNAL_SERVER_ERROR</li>
     * <li>Any other  - HTTP status code INTERNAL_SERVER_ERROR</li>
     * </ul>
     *
     * @param pid Used to determine what to respond
     * @param dsid Ignored.
     * @return The empty string.
     * @throws WebApplicationException For response codes other than 200 OK.
     */
    @GET
    @Path("{pid}/datastreams/{dsid}")
    public String getDatastream(@PathParam("pid") String pid,
                         @PathParam("dsid") String dsid) {
        if (pid.equals("demo:1")) {
            return "";
        } else if (pid.equals("demo:2")) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (pid.equals("demo:3")) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } else if (pid.equals("demo:4")) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
