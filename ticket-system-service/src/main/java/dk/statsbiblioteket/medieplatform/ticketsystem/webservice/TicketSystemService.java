package dk.statsbiblioteket.medieplatform.ticketsystem.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.statsbiblioteket.medieplatform.ticketsystem.BackendException;
import dk.statsbiblioteket.medieplatform.ticketsystem.MissingArgumentException;
import dk.statsbiblioteket.medieplatform.ticketsystem.Ticket;
import dk.statsbiblioteket.medieplatform.ticketsystem.TicketNotFoundException;
import dk.statsbiblioteket.medieplatform.ticketsystem.TicketSystemFacade;

@Path("/tickets/")
public class TicketSystemService {
    private TicketSystemFacade tcksys; 

    private final Logger log = LoggerFactory.getLogger(TicketSystemService.class);

    public TicketSystemService() throws BackendException {
        tcksys = TicketSystemFacade.getInstance(); 
        log.trace("Created a new TicketSystemService object");
    }

    /*Issuing of tickets*/

    @GET
    @Path("issueTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Map<String, String> issueTicketGet(
            @QueryParam("id") List<String> id,
            @QueryParam("type") String type,
            @QueryParam("ipAddress") String ipAddress,
            @Context UriInfo uriInfo
    ) throws MissingArgumentException {
        return issueTicketQueryParams(id, type, ipAddress, uriInfo);
    }

    @POST
    @Path("issueTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Map<String, String> issueTicketQueryParams(
            @QueryParam("id") List<String> resources,
            @QueryParam("type") String type,
            @QueryParam("ipAddress") String ipAddress,
            @Context UriInfo uriInfo
    ) throws MissingArgumentException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        queryParams.remove("id");
        queryParams.remove("type");
        queryParams.remove("ipAddress");

        if (resources == null){
            throw new MissingArgumentException("id is missing");
        }

        if (type == null){
            throw new MissingArgumentException("type is missing");
        }

        if (ipAddress == null){
            throw new MissingArgumentException("ipAddress is missing");
        }

        Map<String, List<String>> userAttributes = new HashMap<String, List<String>>();

        for (String key : queryParams.keySet()) {
            List<String> values = queryParams.get(key);
            if (values != null && values.size() > 0) {
                userAttributes.put(key, values);
            }
        }
        
        return tcksys.issueTicket(resources, type, ipAddress, userAttributes);
    }

    /*Resolving of tickets*/


    @GET
    @Path("resolveTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Ticket resolveTicket(@QueryParam("ticket") String ticketID) throws TicketNotFoundException {
    	log.debug("Resolving ticket with id: '{}'", ticketID);
        return tcksys.resolveTicket(ticketID);
    }

    @GET
    @Path("resolveTicket/{ticket}")
    @Produces({MediaType.APPLICATION_JSON})
    public Ticket resolveTicketAlt(@PathParam("ticket") String ticketID) throws TicketNotFoundException {
        return resolveTicket(ticketID);
    }




}
