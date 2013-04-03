package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.exceptions.BackendException;
import dk.statsbiblioteket.doms.authchecker.exceptions.MissingArgumentException;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.Authorization;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.Ticket;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.TicketNotFoundException;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/25/11
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/tickets/")
public class TicketSystem {

    private static dk.statsbiblioteket.doms.authchecker.ticketissuer.TicketSystem tickets;
    private static Authorization authorization;

    private static final Object lock = new Object();

    private static final String TICKET_TTL_PROP = "dk.statsbiblioteket.doms.authchecker.tickets.timeToLive";

    private Log log = LogFactory.getLog(TicketSystem.class);



    public TicketSystem() throws BackendException {
        log.trace("Created a new TicketSystem webservice object");
        synchronized (lock){
            if (tickets == null){
                long ttl;
                try {
                    String ttlString = ConfigCollection.getProperties()
                            .getProperty(TICKET_TTL_PROP,""+30*1000);
                    log.trace("Read '"+TICKET_TTL_PROP+"' property as '"+ttlString+"'");
                    ttl = Long.parseLong(ttlString);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse the  '"+ TICKET_TTL_PROP
                             +"' as a long, using default 30 sec timetolive",e);
                    ttl = 30*1000;
                }
                tickets = new dk.statsbiblioteket.doms.authchecker.ticketissuer.TicketSystem(ttl);

                String authService = ConfigCollection.getProperties().getProperty("TICKET_AUTH_SERVICE");
                authorization = new Authorization(authService);
            }
        }
    }


    /*Issuing of tickets*/

    @GET
    @Path("issueTicket")
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    public String issueTicketGet(){
        log.trace("Entered issueTicketGet with a get request");
        return "You must HTTP POST this url, not HTTP GET it";
    }


    @POST
    @Path("issueTicket")
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    @Deprecated
    public Ticket issueTicketQueryParams(@Context UriInfo ui
    ) throws MissingArgumentException {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        String username = queryParams.getFirst("username");
        if (username == null){
            throw new MissingArgumentException("Username is missing");
        }
        String resource = queryParams.getFirst("resource");
        if (resource == null){
            resource = queryParams.getFirst("url");
        }
        if (resource == null){
            throw new MissingArgumentException("Resource/url is missing");
        }

        log.trace("Entered issueTicket with params '"
                  +username
                  +"' and resource='"
                  + resource +"'");
        Ticket ticket = tickets.issueTicket(username, resource, queryParams);
        log.trace("Issued ticket='"+ticket.getID()+"'");
        return ticket;
    }


    @POST
    @Path("issueTicket")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    @Deprecated
    public Ticket issueTicketFormParams(
            @QueryParam("username") String username,
            @QueryParam("resource") String resource,
            MultivaluedMap<String, String> formParams
    ){
        log.trace("Entered issueTicket with params '"
                  +username
                  +"' and resource='"
                  + resource +"'");
        Ticket ticket = tickets.issueTicket(username, resource, formParams);
        log.trace("Issued ticket='"+ticket.getID()+"'");
        return ticket;
    }



    /*Resolving of tickets*/

    @GET
    @Path("resolveTicket")
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    public Ticket resolveTicket(
            @QueryParam("ID")
            String ID)
            throws TicketNotFoundException {
        log.trace("Entered resolveTicket with param ID='"+ID+"'");
        Ticket ticket = tickets.getTicketFromID(ID);
        if (ticket == null){
            throw new TicketNotFoundException("The ticket ID '"+ID+"' was not found in the system");
        }
        log.trace("Found ticket='"+ticket.getID()+"'");
        return ticket;
    }

    @GET
    @Path("resolveTicket/{ID}")
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    public Ticket resolveTicketAlt(
            @PathParam("ID")
            String ID)
            throws TicketNotFoundException {
        return resolveTicket(ID);
    }




}
