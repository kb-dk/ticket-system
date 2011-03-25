package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.exceptions.BackendException;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.Ticket;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.TicketNotFoundException;
import dk.statsbiblioteket.doms.authchecker.ticketissuer.TicketSystem;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/25/11
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/tickets/")
public class TicketIssuer {

    private static TicketSystem tickets;

    private static final Object lock = new Object();

    private static final String TICKET_TTL_PROP = "dk.statsbiblioteket.doms.authchecker.tickets.timeToLive";

    private Log log = LogFactory.getLog(TicketIssuer.class);



    public TicketIssuer() throws BackendException {
        log.trace("Created a new TicketIssuer webservice object");
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
                tickets = new TicketSystem(ttl);

            }
        }
    }

    @GET
    @Path("issueTicket")
    public String issueTicketGet(){
        log.trace("Entered issueTicketGet with a get request");
        return "You must HTTP POST this url, not HTTP GET it";
    }

    @POST
    @Path("issueTicket")
    @Produces("text/xml")
    public Ticket issueTicketWithProperties(
            @QueryParam("username")
            String username,
            @QueryParam("resource")
            String resource,
            @QueryParam("property")
            List<String> properties
    ){
        log.trace("Entered issueTicket with params '"
                  +username
                  +"' and resource='"
                  + resource +"'");
        Ticket ticket = tickets.issueTicket(username, resource, properties.toArray(new String[0]));
        log.trace("Issued ticket='"+ticket.getID()+"'");
        return ticket;
    }



    @GET
    @Path("resolveTicket")
    @Produces("text/xml")
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
    @Produces("text/xml")
    public Ticket resolveTicketAlt(
            @PathParam("ID")
            String ID)
            throws TicketNotFoundException {
        return resolveTicket(ID);
    }


}
