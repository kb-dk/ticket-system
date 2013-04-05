package dk.statsbiblioteket.medieplatform.ticketsystem;

import dk.statsbiblioteket.util.caching.TimeSensitiveCache;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr + mar
 * Date: Oct 7, 2010
 * Time: 3:19:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketSystem {


    private TimeSensitiveCache<String, Ticket> tickets;
    private Authorization authorization;

    public TicketSystem(long timeToLive, Authorization authorization) {
        this.tickets = new TimeSensitiveCache<String, Ticket>(timeToLive, false);//30 sec
        this.authorization = authorization;
    }


    /**
     * Get the ticket from the system
     * @param id the id of the ticket
     * @return the ticket, or null of the ticket is not found
     */
    public Ticket getTicketFromID(String id){
        return tickets.get(id);
    }

    public Ticket issueTicket(List<String> resources,
                              String type,
                              String userIdentifier,
                              Map<String, List<String>> userAttributes) {

        List<String> authorizedResources = authorization.authorizeUser(userAttributes, type, resources);
        Ticket ticket = new Ticket(type, userIdentifier, authorizedResources, userAttributes);
        if (!ticket.getResources().isEmpty()) {//No need to preserve the ticket, if it does not allow access to anything
            tickets.put(ticket.getID(), ticket);
        }
        return ticket;
    }
}
